/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.jobs.lib_v1.imageloader.core.display;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.jobs.lib_v1.imageloader.core.DisplayImageOptions;
import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.imageloader.core.assist.LoadedFrom;

/**
 * Fake displayer which doesn't display Bitmap in ImageView. Should be used in {@linkplain DisplayImageOptions display
 * options} for
 * {@link ImageLoader#loadImage(String, com.jobs.lib_v1.imageloader.core.assist.ImageSize, com.jobs.lib_v1.imageloader.core.DisplayImageOptions, com.jobs.lib_v1.imageloader.core.assist.ImageLoadingListener)}
 * ImageLoader.loadImage()}
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.6.0
 */
public final class FakeBitmapDisplayer implements BitmapDisplayer {
	@Override
	public Bitmap display(Bitmap bitmap, ImageView imageView, LoadedFrom loadedFrom) {
		// Do nothing
		return bitmap;
	}
}
