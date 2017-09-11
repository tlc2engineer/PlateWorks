package com.plateworks;

import tech.Place;
import tech.Yard;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import static com.plateworks.MapFragment.allWidth;
import static tech.Place.emptyPlace;
import static com.plateworks.MapFragment.actColumn;
import static com.plateworks.MapFragment.actRow;

/**
 * Класс отображения карты склада.
 */
public final class PlateMap  extends View {
    // Высота ряда
	private final int rowHeight=120;
    // paints
	public static Paint blackLine=new Paint();
	public static Paint doubleLine=new Paint();
	public static Paint textP=new Paint();
	public static Paint fillGr=new Paint();
	Paint grayP=new Paint();
    // ширина и высота
	private int width;
	private int height;
    // context
	private Context context;

    /**
     * Конструктор
     * @param context
     */
	public PlateMap(final Context context) {
		super(context);
		this.context=context;
		blackLine.setColor(Color.BLACK);
		blackLine.setStrokeWidth(1);
		blackLine.setStyle(Paint.Style.STROKE);
		doubleLine.setColor(Color.BLACK);
		doubleLine.setStrokeWidth(3);
		doubleLine.setStyle(Paint.Style.STROKE);
		fillGr.setStyle(Paint.Style.FILL);
		fillGr.setColor(Color.GREEN);
		textP.setStyle(Paint.Style.FILL);
		textP.setTextSize(46);
		grayP.setColor(Color.GRAY);
		this.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action =event.getAction();
				float x=event.getX();
				float y=event.getY();
				float widthC=(float)allWidth/(86-18);
				 actRow=(int) ((y-rowHeight)/((float)(height-rowHeight)/8)+1);
				 actColumn=(int) (x/widthC+1)+18;
				 PlateMap.this.invalidate();
				return false;
			}
			
		});
	}

    /**
     * Отрисовка карты
     * @param canvas
     */
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
        // Получение ширины и высоты
		width=this.getWidth();
		height=this.getHeight();
		// ширина колонны
		float widthC=(float)allWidth/(86-18);
        // заполнение данными
		for( int column=1;column<=(86-18);column++){
			canvas.drawRect((column-1)*((float)allWidth/(86-18)), 0,(column)*((float)allWidth/(86-18)), rowHeight, blackLine);
			Rect bounds = new Rect();
			textP.getTextBounds(String.valueOf(column+18), 0, String.valueOf(column+18).length(), bounds);
			canvas.drawText(""+(column+18), (column-1)*(widthC)+(widthC-bounds.width())/2, rowHeight/2+bounds.height()/2, textP);
		}
		for( int column=1;column<=(86-18);column++){
			for(int row=1;row<9;row++){
				PlaceCell cell=new PlaceCell(column+18,row); 
				cell.draw(canvas, (column-1)*((float)allWidth/(86-18)),rowHeight+(row-1)*(float)(height-rowHeight)/8,(float)allWidth/(86-18), (float)(height-rowHeight)/8);
			}
		}
	}

    /**
     * Класс ячейки карты одного места
     */
	private class PlaceCell{
        //место
		Place place;
        // ряд и колонна
		private int column;
		private int row;

        /**
         * Конструктор
         * @param column
         * @param row
         */
		public PlaceCell(int column,int row){
			this.column=column;
			this.row=row;
			place=Yard.getInstance().getPlace(column, row);
		}

        /**
         * Отрисовка ячейки по координатам
         * @param canvas
         * @param x
         * @param y
         * @param width
         * @param height
         */
		public void draw(Canvas canvas,float x,float y,float width,float height){
			
			Rect bounds = new Rect();
			 textP.getTextBounds(String.valueOf("" + place.listCount), 0, String.valueOf("" + place.listCount).length(), bounds);
			if(!place.equals(emptyPlace)) {
				if (column == actColumn && row == actRow) {
					canvas.drawRect(x, y, x+width, y+height, fillGr);
				}
				canvas.drawText("" + place.listCount, x + (width - bounds.width()) / 2, y + (height) / 2 + bounds.height() / 2, textP);
				}
			else{
				canvas.drawRect(x, y, x+width, y+height, grayP);
			}
			if(column==actColumn && row==actRow)
				canvas.drawRect(x, y, x+width, y+height, doubleLine);
			else
				canvas.drawRect(x, y, x+width, y+height, blackLine);
		}
	}

    /**
     * Получение текущей колонны
     * @return
     */
	public int getActColumn() {
		return actColumn;
	}

    /**
     * Получение текущего ряда.
     * @return
     */
	public int getActRow() {
		return actRow;
	}
	
}
