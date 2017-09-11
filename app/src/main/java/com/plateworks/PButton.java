package com.plateworks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Button;

/**
 * Класс кнопки с изображением +-
 */
public class PButton extends Button {
// Состояние +-
	private boolean plus=true;

	/**
	 * Конструктор.
	 * @param context
	 */
	public PButton(Context context) {
		super(context);
	}

	/**
	 * Установка флага +
	 * @param plus флаг
	 */
	public void setP(boolean plus){
	this.plus=plus;
}

	/**
	 * Отрисовка кнопки
	 * @param canvas
	 */
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		this.setBackgroundColor(Color.WHITE);
		int h = canvas.getHeight();
		int w=canvas.getWidth();
		Paint paint=new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(2,2, w-2,h-2,paint);
			canvas.drawLine(w/4, h/2, w-w/4, h/2, paint);
		if(plus)
			canvas.drawLine(w/2, h/4, w/2, h-h/4, paint);
	}
	

}
