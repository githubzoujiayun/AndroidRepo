package com.jobs.lib_v1.data.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

import com.jobs.lib_v1.app.AppUtil;

/**
 * 前程无忧 常规压缩/解压类 Java 版
 * 
 * @author solomon.wen
 * @date 2013-12-21
 */
public final class CQCompress {
	/**
	 * zlib 压缩数据
	 * 以 zlib 算法的最高压缩率进行数据压缩
	 * 
	 * @param bytes 需要压缩的数据
	 * @return byte[] 返回压缩好的内容，压缩失败则返回 null
	 */
	public static byte[] zlibCompress(byte[] bytes) {
		return zlibCompress(bytes, Deflater.BEST_COMPRESSION);
	}

	/**
	 * zlib 压缩数据
	 * 
	 * @param bytes 需要压缩的数据
	 * @param level 压缩的级别
	 * @return byte[] 返回压缩好的内容，压缩失败则返回 null
	 */
	public static byte[] zlibCompress(byte[] bytes, int level) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
			Deflater def = new Deflater(level);
			OutputStream out = new DeflaterOutputStream(dataOut, def);

			writeInputStream(in, out);
			in.close();

			def.end();

			return dataOut.toByteArray();
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}
   
	/**
	 * zlib 解压数据
	 * 
	 * @param compressed_bytes
	 * @return byte[] 解压成功后返回解压的内容，解压失败则返回 null
	 */
	public static byte[] zlibDecompress(byte[] compressed_bytes) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(compressed_bytes);
			InflaterInputStream zIn = new InflaterInputStream(in);
			ByteArrayOutputStream dataOut = new ByteArrayOutputStream();

			writeInputStream(zIn, dataOut);

			zIn.close();
			in.close();
	 
			return dataOut.toByteArray();
		} catch (Throwable e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * gzip 压缩数据
	 * 
	 * @param bytes 需要压缩的数据
	 * @return byte[] 返回压缩好的内容，压缩失败则返回 null
	 */
	public static byte[] gzipCompress(byte[] bytes) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream gout = new GZIPOutputStream(out);

			writeInputStream(in, gout);

			// Close the file and stream
			gout.finish();
			gout.close();

			return out.toByteArray();
		} catch (IOException e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * gzip 解压数据
	 * 
	 * @param compressed_bytes
	 * @return byte[] 解压成功后返回解压的内容，解压失败则返回 null
	 */
	public static byte[] gzipDecompress(byte[] compressed_bytes) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(compressed_bytes);
			GZIPInputStream gin = new GZIPInputStream(in);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			writeInputStream(gin, out);

			// Close the file and stream
			gin.close();

			return out.toByteArray();
		} catch (IOException e) {
			AppUtil.print(e);
		}

		return null;
	}

	/**
	 * 把一个可读数据流中的数据写入到另一个可写数据流中，直至写完为止
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private static void writeInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[4096];
		int len;
		while ((len = in.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
	}
}
