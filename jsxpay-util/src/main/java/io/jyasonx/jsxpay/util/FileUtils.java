package io.jyasonx.jsxpay.util;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.jyasonx.jsxpay.util.json.JsonProviderHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.tuple.Triple;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * File Utils.
 */
@Slf4j
public class FileUtils {
    public static final String EXT_ZIP = "zip";
    public static final String EXT_CSV = "csv";
    public static final String EXT_XLS = "xls";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    private static final int BUFFER_LEN = 8024;

    private FileUtils() {
    }

    /**
     * Compress files by zip.
     */
    public static void compress2Zip(List<Triple<String, Integer, InputStream>> files, File destFile) {
        try (ZipArchiveOutputStream out = new ZipArchiveOutputStream(
                new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_LEN))) {
            files.forEach(f -> {
                try (InputStream is = new BufferedInputStream(f.getRight(), BUFFER_LEN)) {
                    ZipArchiveEntry entry = new ZipArchiveEntry(f.getLeft());
                    entry.setSize(f.getMiddle());
                    out.putArchiveEntry(entry);
                    IOUtils.copy(is, out);
                } catch (IOException err) {
                    throw new FilesException(err);
                }
            });
            out.closeArchiveEntry();
        } catch (IOException err) {
            throw new FilesException(err);
        }
    }

    /**
     * Compress file by zip.
     */
    public static void compress2Zip(File file, File destFile) {
        try (ZipArchiveOutputStream out = new ZipArchiveOutputStream(
                new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_LEN));
             InputStream is = new BufferedInputStream(new FileInputStream(file), BUFFER_LEN)) {

            ZipArchiveEntry entry = new ZipArchiveEntry(file.getName());
            entry.setSize(file.length());
            out.putArchiveEntry(entry);
            IOUtils.copy(is, out);
            out.closeArchiveEntry();
        } catch (IOException err) {
            throw new FilesException(err);
        }
    }

    /**
     * Decompress file from zip.
     */
    public static void decompressZip(File file, File dir) {
        try (ZipArchiveInputStream is = new ZipArchiveInputStream(
                new BufferedInputStream(new FileInputStream(file), BUFFER_LEN))) {
            ZipArchiveEntry entry;
            while ((entry = is.getNextZipEntry()) != null) {
                if (entry.isDirectory()) {
                    File directory = new File(dir, entry.getName());
                    boolean folderExisted = directory.exists() || directory.mkdirs();
                    if (!folderExisted) {
                        throw new IOException("Unable to create path:" + directory.getAbsolutePath());
                    }
                } else {
                    try (OutputStream os = new BufferedOutputStream(
                            new FileOutputStream(new File(dir, entry.getName())), BUFFER_LEN)) {
                        IOUtils.copy(is, os);
                    }
                }
            }
        } catch (IOException err) {
            throw new FilesException(err);
        }
    }

    /**
     * Decompress file from zip.
     */
    public static byte[] decompressZip(byte[] file) {
        try (ZipArchiveInputStream is = new ZipArchiveInputStream(
                new BufferedInputStream(new ByteArrayInputStream(file)))) {
            ZipArchiveEntry entry;
            while ((entry = is.getNextZipEntry()) != null) {
                if (!entry.isDirectory()) {
                    return IOUtils.toByteArray(is);
                }
            }

            throw new FileNotFoundException();
        } catch (IOException err) {
            throw new FilesException(err);
        }
    }

    /**
     * Read last line from text file.
     */
    public static String readLastLine(File file, String charset) {
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            return null;
        }
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long len = raf.length();
            if (len == 0L) {
                return "";
            } else {
                long pos = len - 1;
                while (pos > 0) {
                    pos--;
                    raf.seek(pos);
                    if (raf.readByte() == '\n') {
                        break;
                    }
                }
                if (pos == 0) {
                    raf.seek(0);
                }
                byte[] bytes = new byte[(int) (len - pos)];
                if (raf.read(bytes) <= 0) {
                    return "";
                }
                if (charset == null) {
                    return new String(bytes, StandardCharsets.UTF_8);
                } else {
                    return new String(bytes, charset);
                }
            }
        } catch (IOException err) {
            throw new FilesException(err);
        }
    }

    /**
     * Get ExtensionName.
     */
    public static String getExtensionName(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1 || index == fileName.length() - 1) {
            return "";
        } else {
            return fileName.substring(index + 1);
        }
    }

    /**
     * Get file name.
     */
    public static String getFileName(String filePath) {
        int index = filePath.lastIndexOf(File.separator);
        if (index == -1 || index == filePath.length() - 1) {
            return "";
        } else {
            return filePath.substring(index + 1);
        }
    }

    /**
     * Write file headers.
     */
    public static void writeHeader(String fileName, String titles) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName, true), StandardCharsets.UTF_8))) {
            writer.append(titles);
            writer.flush();
        } catch (IOException err) {
            log.error("Failed to write file header!", err);
        }
    }

    /**
     * Write csv data to file.
     */
    public static void writeDataToFile(List<Map<String, Object>> data, String fileName) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName, true), StandardCharsets.UTF_8))) {
            CsvSchema.Builder schema = new CsvSchema.Builder();
            CsvMapper mapper = new CsvMapper();
            mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
            if (!data.isEmpty()) {
                for (String value : data.get(0).keySet()) {
                    schema.addColumn(value, CsvSchema.ColumnType.STRING);
                }
                mapper.writer(schema.build()).writeValue(writer, data);
            }
        } catch (IOException err) {
            log.error("Failed to write File!", err);
        }
    }

    /**
     * append data to file.
     */
    public static void writeBody(List<Object> data,
                                 String fileName,
                                 String columnKeys) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName, true), StandardCharsets.UTF_8))) {
            CsvSchema.Builder schema = new CsvSchema.Builder().disableQuoteChar();
            CsvMapper mapper = new CsvMapper();
            mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
            List<String> usedColumns = Arrays.asList(columnKeys.split(StringUtils.COMMA));
            List<Map> mapList = convert2Maps(data, usedColumns);

            if (!data.isEmpty()) {
                usedColumns.forEach(t -> schema.addColumn(t, CsvSchema.ColumnType.STRING));
                mapper.writer(schema.build()).writeValue(writer, mapList);
            }
        } catch (IOException err) {
            log.error("Failed to write File!", err);
            throw new FilesException("Failed to write File!", err);
        }
    }

    /**
     * write head and data to file.
     */
    public static void writeCsv(List<Object> data,
                                String fileName,
                                String titles,
                                String columnKeys) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            writer.append(titles);
            writer.flush();
            CsvSchema.Builder schema = new CsvSchema.Builder().disableQuoteChar();
            CsvMapper mapper = new CsvMapper();
            mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));

            List<String> usedColumns = Arrays.asList(columnKeys.split(StringUtils.COMMA));
            List<Map> mapList = convert2Maps(data, usedColumns);

            if (!data.isEmpty()) {
                usedColumns.forEach(t -> schema.addColumn(t, CsvSchema.ColumnType.STRING));
                mapper.writer(schema.build()).writeValue(writer, mapList);
            }
        } catch (IOException err) {
            log.error("Failed to write File!", err);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Map> convert2Maps(List<Object> list, List<String> usedColumns) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map> mapList = list.stream()
                .map(o -> JsonProviderHolder.JACKSON.convertObj(o, Map.class))
                .collect(Collectors.toList());
        Set<String> unusedColumns = getUnusedKeys(mapList.get(0).keySet(), usedColumns);
        mapList.forEach(map -> unusedColumns.forEach(map::remove));
        return mapList;
    }

    private static Set<String> getUnusedKeys(Set<String> allKeys, List<String> usedKeys) {
        Set<String> unusedKeys = new HashSet<>(allKeys);
        unusedKeys.removeAll(usedKeys);
        return unusedKeys;
    }

    /**
     * Get MimeFile type.
     */
    public static String getContentTypeForMimeFile(File file) {
        return new MimetypesFileTypeMap().getContentType(file);
    }
}
