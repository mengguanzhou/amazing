﻿/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wiipu.shopping.barcode.view;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.example.barcodeuploadapp.R;
import com.google.zxing.ResultPoint;
import com.wiipu.shopping.barcode.camera.CameraManager;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View
{

	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192, 128, 64 };
	private static final long ANIMATION_DELAY = 100L;
	private static final int OPAQUE = 0xFF;

	private final Paint paint;
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private final int frameColor;
	private final int laserColor;
	private final int resultPointColor;
	private int scannerAlpha;
	// 添加中间滑动线的最低端和最高端位置 , 中间那条线每次刷新移动的距离
	private static final int SPEEN_DISTANCE = 15;
	private int slideTop;
	private int slideBottom;
	private Boolean isFirst;
	private static final int TEXT_SIZE = 16;// 字体大小
	private static float density;// 屏幕密度
	private static final int TEXT_PADDING_TOP = 60; // 字体距离扫描框下面的距离

	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;
	//条形码
    public static final int BARCODE=1;
    //二维码
    public static final int BARCODE_2D=2;
	public static final int LOGISTICS = 3;
    private int type;
	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		frameColor = resources.getColor(R.color.viewfinder_frame);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		scannerAlpha = 0;
		possibleResultPoints = new HashSet<ResultPoint>(5);
		isFirst = false;
		density = context.getResources().getDisplayMetrics().density;
	}
    public void setType(int type){
    	this.type=type;
    }
	@Override
	public void onDraw(Canvas canvas)
	{
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null)
		{
			return;
		}
		// 初始化中间线滑动的最上边和最下边
		if (!isFirst)
		{
			isFirst = true;
			slideBottom = frame.bottom;
			slideTop = frame.top;
		}

		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// //扫描框四个角和扫描线条修改;
		// paint.setColor(getResources().getColor(R.color.green));
		// //左上角
		// canvas.drawRect(frame.left,frame.top,frame.left+40,frame.top+10,
		// paint);
		// canvas.drawRect(frame.left,frame.top,frame.left+10,frame.top+40,
		// paint);
		// //左下角
		// canvas.drawRect(frame.left,frame.bottom-10,frame.left+40,frame.bottom,paint);
		// canvas.drawRect(frame.left,frame.bottom-40,frame.left+10,frame.bottom,paint);
		// //右上角
		// canvas.drawRect(frame.right-10, frame.top,
		// frame.right,frame.top+40,paint);
		// canvas.drawRect(frame.right-40, frame.top, frame.right, frame.top+10,
		// paint);
		// //右下角
		// canvas.drawRect(frame.right-10,frame.bottom-40,frame.right,frame.bottom,
		// paint);
		// canvas.drawRect(frame.right-40,frame.bottom-10,frame.right,frame.bottom,
		// paint);

		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null)
		{
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else
		{

			// Draw a two pixel solid black border inside the framing rect
			paint.setColor(frameColor);
			canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
			canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
			canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
			canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

			// Draw a red "laser scanner" line through the middle to show
			// decoding is active
			paint.setColor(laserColor);
			paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
			scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
			int middle = frame.height() / 2 + frame.top;
			canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
			// 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
			/*
			 * slideTop+=SPEEN_DISTANCE; if(slideTop>=frame.bottom){
			 * slideTop=frame.top; } canvas.drawRect(frame.left + 5,slideTop-3,
			 * frame.right - 5, slideTop + 3, paint);
			 */
			// 画扫描框下面的字
			paint.setColor(Color.WHITE);
			paint.setTextSize(TEXT_SIZE * density);
			paint.setAlpha(0x80);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			String text ;
			if(type==1){
				//条形码
				text= getResources().getString(R.string.barcode_text);
			}
			else {
				//二维码
				text= getResources().getString(R.string.barcode_text);
			}
			
			float textWidth = paint.measureText(text);
			canvas.drawText(text, (width - textWidth) / 2, (float) (frame.bottom + (float) TEXT_PADDING_TOP * density),
					paint);

			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty())
			{
				lastPossibleResultPoints = null;
			} else
			{
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible)
				{
					canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null)
			{
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast)
				{
					canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
				}
			}

			// Request another update at the animation interval, but only
			// repaint the laser line,
			// not the entire viewfinder mask.
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
		}
	}

	public void drawViewfinder()
	{
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode)
	{
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point)
	{
		possibleResultPoints.add(point);
	}

}
