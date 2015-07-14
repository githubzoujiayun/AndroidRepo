/*  MonkeyTalk - a cross-platform functional testing tool
    Copyright (C) 2012 Gorilla Logic, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.gorillalogic.monkeytalk.utils.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.junit.Test;

import com.gorillalogic.monkeytalk.CommandWorld;
import com.gorillalogic.monkeytalk.utils.FileUtils;

public class FileUtilsTest {

	@Test
	public void testRemoveExtension() {
		assertThat(FileUtils.removeExt("foo.mt", CommandWorld.SCRIPT_EXT), is("foo"));
		assertThat(FileUtils.removeExt("foo.MT", CommandWorld.SCRIPT_EXT), is("foo"));
		assertThat(FileUtils.removeExt("FOO.MT", CommandWorld.SCRIPT_EXT), is("FOO"));
		assertThat(FileUtils.removeExt("foo.mts", CommandWorld.SCRIPT_EXT), is("foo.mts"));
		assertThat(FileUtils.removeExt(".mt", CommandWorld.SCRIPT_EXT), is(""));
		assertThat(FileUtils.removeExt("", CommandWorld.SCRIPT_EXT), is(""));
		assertThat(FileUtils.removeExt(null, CommandWorld.SCRIPT_EXT), nullValue());
	}

	@Test
	public void testZip() throws IOException {
		File zipDir = new File("bin/tests/test/zip");
		File zip = FileUtils.zipDirectory(zipDir, true, true);
		File unzipDir = new File("bin/tests/test/unzip");
		FileUtils.unzipFile(zip, unzipDir);
	}

	@Test
	public void testCopyFile() throws IOException {
		File dir = FileUtils.tempDir();
		File foo = new File(dir, "foo.mt");
		FileUtils.writeFile(foo, "Button FOO Tap");
		File bar = new File(dir, "bar.mt");

		FileUtils.copyFile(foo, bar);

		assertThat(FileUtils.readFile(bar), is("Button FOO Tap"));
	}

	@Test
	public void testCopyDir() throws IOException {
		File dir = FileUtils.tempDir();
		File fooDir = new File(dir, "foo");
		File foo = new File(fooDir, "foo.mt");
		FileUtils.writeFile(foo, "Button FOO Tap");
		File bar = new File(fooDir, "bar.mt");
		FileUtils.writeFile(bar, "Button BAR Tap");
		File bazDir = new File(fooDir, "baz");
		File baz = new File(bazDir, "baz.mt");
		FileUtils.writeFile(baz, "Button BAZ Tap");

		File dest = new File(dir, "dest");
		File destBaz = new File(dest, "baz");
		assertThat(dest.exists(), is(false));

		FileUtils.copyDir(fooDir, dest);
		assertThat(dest.exists(), is(true));
		assertThat(dest.isDirectory(), is(true));
		assertThat(destBaz.exists(), is(true));
		assertThat(destBaz.isDirectory(), is(true));
		assertThat(dest.list().length, is(3));
		assertThat(destBaz.list().length, is(1));

		assertThat(FileUtils.readFile(new File(dest, "foo.mt")), is("Button FOO Tap"));
		assertThat(FileUtils.readFile(new File(dest, "bar.mt")), is("Button BAR Tap"));
		assertThat(FileUtils.readFile(new File(destBaz, "baz.mt")), is("Button BAZ Tap"));
	}

	@Test
	public void testCopyDirWithFilter() throws IOException {
		File dir = FileUtils.tempDir();
		File fooDir = new File(dir, "foo");
		File foo = new File(fooDir, "foo.mt");
		FileUtils.writeFile(foo, "Button FOO Tap");
		File bar = new File(fooDir, "bar.mt");
		FileUtils.writeFile(bar, "Button BAR Tap");
		File bazDir = new File(fooDir, "baz");
		File baz = new File(bazDir, "baz.mt");
		FileUtils.writeFile(baz, "Button BAZ Tap");

		// Filter #1 -- all dirs AND all .mt files
		File dest = new File(dir, "dest");
		File destBaz = new File(dest, "baz");
		assertThat(dest.exists(), is(false));

		FileUtils.copyDir(fooDir, dest, new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".mt");
			}
		});
		assertThat(dest.exists(), is(true));
		assertThat(dest.isDirectory(), is(true));
		assertThat(dest.list().length, is(3));
		assertThat(destBaz.exists(), is(true));
		assertThat(destBaz.isDirectory(), is(true));
		assertThat(destBaz.list().length, is(1));

		assertThat(FileUtils.readFile(new File(dest, "foo.mt")), is("Button FOO Tap"));
		assertThat(FileUtils.readFile(new File(dest, "bar.mt")), is("Button BAR Tap"));
		assertThat(FileUtils.readFile(new File(destBaz, "baz.mt")), is("Button BAZ Tap"));

		// Filter #2 -- only .mt files (so baz dir is excluded)
		File dest2 = new File(dir, "dest2");
		FileUtils.copyDir(fooDir, dest2, new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".mt");
			}
		});
		assertThat(dest2.exists(), is(true));
		assertThat(dest2.isDirectory(), is(true));
		assertThat(dest2.list().length, is(2));
		assertThat(new File(dest2, "baz").exists(), is(false));

		assertThat(FileUtils.readFile(new File(dest2, "foo.mt")), is("Button FOO Tap"));
		assertThat(FileUtils.readFile(new File(dest2, "bar.mt")), is("Button BAR Tap"));

		// Filter #3 -- only .mt files (so baz dir is excluded)
		File dest3 = new File(dir, "dest3");
		File dest3Baz = new File(dest3, "baz");
		FileUtils.copyDir(fooDir, dest3, new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().startsWith("baz");
			}
		});
		assertThat(dest3.exists(), is(true));
		assertThat(dest3.isDirectory(), is(true));
		assertThat(dest3.list().length, is(1));
		assertThat(dest3Baz.exists(), is(true));
		assertThat(dest3Baz.isDirectory(), is(true));
		assertThat(dest3Baz.list().length, is(1));

		assertThat(FileUtils.readFile(new File(dest3Baz, "baz.mt")), is("Button BAZ Tap"));
	}
}