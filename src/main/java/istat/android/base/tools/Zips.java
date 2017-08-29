package istat.android.base.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 
 * @author toshiba1
 */
public class Zips {

	public static void unZip(String inputFile, String outputFile) {
		UnZipper.unZip(new File(inputFile), new File(outputFile));
	}

	public static void zip(String inputFile, String outputFile) {
		Zipper.zip(new File(inputFile), new File(outputFile));
	}

	public static class UnZipper {

		public static void unZip(File inputFile, File outputFile) {
			try {
				ZipFile zipFile = new ZipFile(inputFile);
				Enumeration<?> enu = zipFile.entries();
				while (enu.hasMoreElements()) {
					ZipEntry zipEntry = (ZipEntry) enu.nextElement();

					String name = zipEntry.getName();
					long size = zipEntry.getSize();
					long compressedSize = zipEntry.getCompressedSize();
					System.out.printf(
							"name: %-20s | size: %6d | compressed size: %6d\n",
							name, size, compressedSize);

					File file = new File(outputFile.getAbsoluteFile() + "/"
							+ name);
					if (name.endsWith("/")) {
						file.mkdirs();
						continue;
					}

					File parent = file.getParentFile();
					if (parent != null) {
						parent.mkdirs();
					}

					InputStream is = zipFile.getInputStream(zipEntry);
					FileOutputStream fos = new FileOutputStream(file);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = is.read(bytes)) >= 0) {
						fos.write(bytes, 0, length);
					}
					is.close();
					fos.close();

				}
				zipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static class Zipper {

		private static void getAllFiles(File dir, List<File> fileList) {
			try {
				File[] files = dir.listFiles();
				for (File file : files) {
					fileList.add(file);
					if (file.isDirectory()) {
						System.out.println("directory:"
								+ file.getCanonicalPath());
						getAllFiles(file, fileList);
					} else {
						System.out.println("     file:"
								+ file.getCanonicalPath());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private static void writeZipFile(File directoryToZip,
				List<File> fileList, File directoryOutput) {

			try {
				FileOutputStream fos =null;
				
				if(directoryOutput.isDirectory()){
					 fos = new FileOutputStream(directoryOutput
					+ "/" + directoryToZip.getName() + ".zip");
				}else
					fos = new FileOutputStream(directoryOutput);

				ZipOutputStream zos = new ZipOutputStream(fos);

				for (File file : fileList) {
					if (!file.isDirectory()) { // we only zip files, not
												// directories
						addToZip(directoryToZip, file, zos);
					}
				}

				zos.close();
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public static void addToZip(File directoryToZip, File file,
				ZipOutputStream zos) throws FileNotFoundException, IOException {
			try {
				FileInputStream fis = new FileInputStream(file);
				String zipFilePath = file.getCanonicalPath().substring(
						directoryToZip.getCanonicalPath().length() + 1,
						file.getCanonicalPath().length());
				System.out.println("Writing '" + zipFilePath + "' to zip file");
				ZipEntry zipEntry = new ZipEntry(zipFilePath);
				zos.putNextEntry(zipEntry);

				byte[] bytes = new byte[1024];
				int length;
				while ((length = fis.read(bytes)) >= 0) {
					zos.write(bytes, 0, length);
				}
			} catch (IOException e) {

			}
			zos.closeEntry();
		}

		public static void zip(File inputFile, File outputFile) {
			List<File> fileList = new ArrayList<File>();
			// System.out.println("---Getting references to all files in: " +
			// directoryToZip.getCanonicalPath());
			getAllFiles(inputFile, fileList);
			// System.out.println("---Creating zip file");
			writeZipFile(inputFile, fileList, outputFile);
			// System.out.println("---Done");
		}

		public static void zip(File[] files, String zipFile) throws IOException {
			String[] _files = new String[files.length];
			int i = 0;
			for (File file : files) {
				_files[i] = file.getAbsolutePath();
				i++;

			}
			zip(_files, zipFile);
		}

		public static void zip(String[] files, String zipFile)
				throws IOException {
			int BUFFER_SIZE = 1024;
			BufferedInputStream origin = null;
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(zipFile)));
			try {
				byte data[] = new byte[BUFFER_SIZE];

				for (int i = 0; i < files.length; i++) {
					FileInputStream fi = new FileInputStream(files[i]);
					origin = new BufferedInputStream(fi, BUFFER_SIZE);
					try {
						ZipEntry entry = new ZipEntry(
								files[i].substring(files[i].lastIndexOf("/") + 1));
						out.putNextEntry(entry);
						int count;
						while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
							out.write(data, 0, count);
						}
					} finally {
						origin.close();
					}
				}
			} finally {
				out.close();
			}
		}
	}
}
