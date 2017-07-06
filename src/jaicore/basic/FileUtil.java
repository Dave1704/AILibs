package jaicore.basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class FileUtil {

	public static List<String> readFileAsList(final String filename) throws IOException {
		try (BufferedReader r = Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8)) {
			String line;
			final List<String> lines = new LinkedList<>();
			while ((line = r.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		}
	}

	public static String readFileAsString(final String filename) throws IOException {
		final StringBuffer sb = new StringBuffer();
		try (BufferedReader r = Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8)) {
			String line;
			while ((line = r.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public static List<List<String>> readFileAsMatrix(final String filename, final String separation) throws IOException {
		final List<String> content = readFileAsList(filename);
		final List<List<String>> matrix = new ArrayList<>(content.size());
		for (final String line : content) {
			final String[] lineAsArray = line.split(separation);
			final List<String> lineAsList = new ArrayList<>(lineAsArray.length);
			for (final String field : lineAsArray) {
				lineAsList.add(field.trim());
			}
			matrix.add(lineAsList);
		}
		return matrix;
	}

	public static boolean deleteFolderRecursively(final File dir) {
		if (dir.isDirectory()) {
			final String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				final boolean success = deleteFolderRecursively(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete(); // The directory is empty now and can be deleted.
	}

	public static void zipFiles(final Collection<String> files, final String archive) throws FileNotFoundException, IOException {

		final FileOutputStream fos = new FileOutputStream(archive);
		final ZipOutputStream zos = new ZipOutputStream(fos);
		final int total = files.size();
		int i = 0;
		for (final String fileName : files) {
			final File file = new File(fileName);
			final FileInputStream fis = new FileInputStream(file);
			final ZipEntry zipEntry = new ZipEntry(file.getName().toString());
			zos.putNextEntry(zipEntry);

			final byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}

			zos.closeEntry();
			fis.close();
			i++;
			System.out.println(i + "/" + total + " ready.");
		}
		zos.close();
		fos.close();
	}
}