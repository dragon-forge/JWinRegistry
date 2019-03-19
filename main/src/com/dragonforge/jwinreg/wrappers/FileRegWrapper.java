package com.dragonforge.jwinreg.wrappers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.dragonforge.jwinreg.RegistryEntry;
import com.dragonforge.jwinreg.RegistryType;

public class FileRegWrapper implements IRegistryWrapper
{
	public final File file, file2;
	private final ExecutorService ioThread = Executors.newFixedThreadPool(1, r ->
	{
		Thread t = Executors.defaultThreadFactory().newThread(r);
		return t;
	});
	
	public FileRegWrapper(File file)
	{
		this.file = file;
		this.file2 = new File(file.getAbsoluteFile() + ".tmp");
		checkFileState();
	}
	
	static final String BASE_COMMENT = "File Registry Wrapper File for JWinRegistry\nhttps://github.com/dragon-forge/JWinRegistry\nStored %,d entries.";
	static final int COMPRESSION = 2;
	
	void checkFileState()
	{
		if(file2.isFile())
			delFile2();
		
		if(!file.isFile())
		{
			file.mkdirs();
			if(file.isDirectory())
				file.delete();
			try(ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file)))
			{
				zos.setLevel(COMPRESSION);
				zos.setComment(String.format(BASE_COMMENT, 0));
				zos.putNextEntry(new ZipEntry(".root"));
				zos.write(200);
				zos.write("JWR".getBytes());
				zos.write(115);
				zos.closeEntry();
			} catch(IOException ioe)
			{
				throw new RuntimeException(ioe);
			}
		}
	}
	
	void copyEntry(ZipFile zf, ZipEntry ze, ZipOutputStream zos) throws IOException
	{
		try(InputStream in = zf.getInputStream(ze))
		{
			byte[] buf = new byte[1024];
			int r;
			
			zos.putNextEntry(ze);
			while((r = in.read(buf)) > 0)
				zos.write(buf, 0, r);
			zos.closeEntry();
		}
	}
	
	void deleteJWR(Predicate<ZipEntry> delete)
	{
		checkFileState();
		
		if(!file2.isFile())
		{
			file2.mkdirs();
			
			if(file2.isDirectory())
				file2.delete();
			
			try(ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file2));ZipFile zf = new ZipFile(file))
			{
				zos.setLevel(COMPRESSION);
				int entries = 0;
				Enumeration<? extends ZipEntry> zes = zf.entries();
				while(zes.hasMoreElements())
				{
					ZipEntry ze = zes.nextElement();
					if(ze.getName().equals(".root") || !delete.test(ze))
					{
						copyEntry(zf, ze, zos);
						++entries;
					}
				}
				zos.setComment(String.format(BASE_COMMENT, entries - 1));
			} catch(IOException ioe)
			{
				throw new RuntimeException(ioe);
			}
			
			try(FileInputStream in = new FileInputStream(file2);FileOutputStream out = new FileOutputStream(file))
			{
				byte[] buf = new byte[1024];
				int r;
				while((r = in.read(buf)) > 0)
					out.write(buf, 0, r);
			} catch(IOException ioe)
			{
				throw new RuntimeException(ioe);
			}
			
			delFile2();
		}
	}
	
	void writeJWR(String path, String key, byte[] value, boolean override)
	{
		checkFileState();
		
		if(!file2.isFile())
		{
			file2.mkdirs();
			if(file2.isDirectory())
				file2.delete();
			
			try(ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file2));ZipFile zf = new ZipFile(file))
			{
				zos.setLevel(COMPRESSION);
				zos.setComment(zf.getComment());
				Enumeration<? extends ZipEntry> zes = zf.entries();
				ZipEntry we = new ZipEntry(path + "/" + key);
				boolean write = true;
				int entries = 0;
				while(zes.hasMoreElements())
				{
					ZipEntry ze = zes.nextElement();
					if(we.equals(ze))
						if(override)
							continue;
						else
							write = false;
					copyEntry(zf, ze, zos);
					++entries;
				}
				if(write)
				{
					zos.putNextEntry(we);
					zos.write(value);
					zos.closeEntry();
					++entries;
				}
				zos.setComment(String.format(BASE_COMMENT, entries - 1));
			} catch(IOException ioe)
			{
				throw new RuntimeException(ioe);
			}
			
			try(FileInputStream in = new FileInputStream(file2);FileOutputStream out = new FileOutputStream(file))
			{
				byte[] buf = new byte[1024];
				int r;
				while((r = in.read(buf)) > 0)
					out.write(buf, 0, r);
			} catch(IOException ioe)
			{
				throw new RuntimeException(ioe);
			}
			
			delFile2();
		}
	}
	
	byte[] readJWR(String path, String key, boolean defaultValue)
	{
		checkFileState();
		if(defaultValue)
			key = ".root";
		try(ZipFile zf = new ZipFile(file))
		{
			ZipEntry ze = zf.getEntry(path + "/" + key);
			if(ze != null)
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				byte[] buf = new byte[1024];
				int r;
				
				try(InputStream in = zf.getInputStream(ze))
				{
					while((r = in.read(buf)) > 0)
						baos.write(buf, 0, r);
				}
				
				return baos.toByteArray();
			}
		} catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
		return new byte[0];
	}
	
	Predicate<ZipEntry> del_ifStartsWith(String path)
	{
		return ze -> ze.getName().startsWith(path);
	}
	
	Predicate<ZipEntry> del_ifNameEquals(String path, String entry)
	{
		return ze -> ze.getName().equals(path + "/" + entry);
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		ioThread.shutdown();
		try
		{
			ioThread.awaitTermination(1L, TimeUnit.DAYS);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		super.finalize();
	}
	
	@Override
	public void deleteParam(String path, String key)
	{
		waitForFuture(ioThread.submit(() -> deleteJWR(del_ifNameEquals(path, key))));
	}
	
	@Override
	public void deletePath(String path)
	{
		waitForFuture(ioThread.submit(() -> deleteJWR(del_ifStartsWith(path))));
	}
	
	@Override
	public void write(RegistryEntry<?> entry, boolean overwrite)
	{
		waitForFuture(ioThread.submit(() -> writeJWR(entry.path, entry.defaultName ? ".root" : entry.key, (entry.type.getId() + " " + entry.stringValue()).getBytes(), overwrite)));
	}
	
	@Override
	public RegistryEntry<?> read(String path, String key, boolean defaultValue)
	{
		String read = new String(waitForFuture(ioThread.submit(() -> readJWR(path, key, defaultValue))));
		String[] subs = read.split(" ", 2);
		RegistryType<?> type = RegistryType.valueOf(subs[0]);
		return new RegistryEntry(path, key, type, type != null ? type.convert(subs[1]) : subs[1]);
	}
	
	@Override
	public void cleanup()
	{
		ioThread.shutdown();
		try
		{
			ioThread.awaitTermination(1L, TimeUnit.DAYS);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		delFile2();
	}
	
	void delFile2()
	{
		if(file2.isFile())
			try
			{
				Files.delete(file2.toPath());
			} catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
	}
	
	<T> T waitForFuture(Future<T> future)
	{
		try
		{
			return future.get();
		} catch(InterruptedException | ExecutionException e)
		{
			return null;
		}
	}
}