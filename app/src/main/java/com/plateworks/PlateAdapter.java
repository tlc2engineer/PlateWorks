package com.plateworks;
import java.util.ArrayList;
import java.util.List;

import tech.Heat;
import tech.Pred;
import tech.Place;
import tech.Plate;
import tech.PlateContainer;
//import android.R;
//import android.R;
//import android.R;
import com.example.plateworks.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * PlateAdapter для ListView. Не используется.
 */
public class PlateAdapter extends BaseAdapter{
private int deepLevel=1;
private int currentOrderNum=0;
private int currentHeatNum=0;
private int currentPlateNum=0;

List<Object> list=new ArrayList();
private Context context;

private Place place;
public void changePlace(Place place){
	this.place=place;
}
public PlateAdapter(Context context,Place place){
	this.context=context;
	this.place=place;
	changeDeepLevel(1);
}
	@Override
	public int getCount() {
		Log.v("ctag",""+list.size()+" plce "+place.getColumn()+":"+place.getRow()+"   "+place.getPlates().size());
		return list.size();
	}

	@Override
	public Object getItem(int position) {
	return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return getItem(position).hashCode();
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final PlateContainer  item=(PlateContainer) getItem(position);
		
		//LinearLayout layout=new LinearLayout(context);
		LinearLayout inview = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.order_item_layout, null);
		LinearLayout ll=new LinearLayout(context);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		if(false){
			if(item instanceof Pred){
				final Pred order=(Pred) list.get(position);

				
				return ll;
				//return orderView;
			}
			else{
			TextView tv = new TextView(context);
			tv.setText(item.toString());
			return tv;
			}
		}
		
		//-----------------------------Pred-----------------------------------
		if(item instanceof Pred){
		final Pred pred=(Pred) list.get(position);
		LinearLayout orderView = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.pred_item, null);
			if(position>0 &&  (getItem(position-1) instanceof Pred) ){
				View dv = orderView.findViewById(R.id.head_pred_item);
				orderView.removeView(dv);
			}
		orderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		ll.addView(orderView);
		TextView view=(TextView) orderView.findViewById(R.id.item_order_text);
            TextView predView=(TextView) orderView.findViewById(R.id.pred_text);
            TextView countView=(TextView) orderView.findViewById(R.id.item_order_count);
            TextView massView=(TextView) orderView.findViewById(R.id.item_order_mass);

          countView.setText(""+pred.getCount());
			massView.setText(String.format("%.2f",pred.getMass()));


            PButton plus=new PButton(context);
		if(position==currentOrderNum && deepLevel>1) plus.setP(false);
		else
			plus.setP(true);
		plus.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(currentOrderNum==place.getOrders().indexOf(pred) && deepLevel>1){
					changeDeepLevel(1);
				}
				else{
				currentOrderNum=place.getOrders().indexOf(pred);
				changeDeepLevel(2);
				}
				PlateAdapter.this.notifyDataSetChanged();
			}
			
		});
		view.setText(" "+pred.getOrdNumber());
            predView.setText(" "+pred.getNumber());
		LinearLayout llpb=(LinearLayout) orderView.findViewById(R.id.item_order_pb);
		llpb.addView(plus);
		//layout.addView(view);
		CheckBox cb=(CheckBox) orderView.findViewById(R.id.item_order_check);
		cb.setChecked(item.isSelected());
		cb.setOnCheckedChangeListener( new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				item.setSelected(isChecked);
				PlateAdapter.this.notifyDataSetChanged();
				
			}
			
		});
		return ll;
		}
		//------------------------------------Heat------------------------------------------
		if(item instanceof Heat){
			LinearLayout heatView = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.heat_item_layout, null);
			if(  (getItem(position-1) instanceof Heat) ){
				View dv = heatView.findViewById(R.id.head_heat_item);
				heatView.removeView(dv);
			}
			heatView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			ll.addView(heatView);
			TextView view=(TextView) heatView.findViewById(R.id.item_heat_text);
			final Heat heat=(Heat) item;
			final int heatNum= place.getOrders().get(currentOrderNum).getHeats().indexOf(heat);
			view.setText(heat.toString());
			TextView gradeText = (TextView) heatView.findViewById(R.id.item_grade_text);
			gradeText.setText(heat.getGrade());
			PButton plus=new PButton(context);
			if(deepLevel>2 && currentHeatNum==heatNum){
				plus.setP(false);//plus.setText("- ");//plus.setText("-");
			}
			else
				plus.setP(true);
				//plus.setText("+");
			plus.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					//MActivity.cashe.setHeat(heat);
					if(currentHeatNum==heatNum && deepLevel>2){
						changeDeepLevel(2);
					}
					else
					{
					currentHeatNum=heatNum;
					changeDeepLevel(3);
					
					}
					PlateAdapter.this.notifyDataSetChanged();
				}
				
			});
			LinearLayout llpb=(LinearLayout) heatView.findViewById(R.id.item_heat_pb);
			llpb.addView(plus);
			//layout.addView(view);
			CheckBox cb=(CheckBox) heatView.findViewById(R.id.item_heat_check);
			cb.setChecked(item.isSelected());
			cb.setOnCheckedChangeListener( new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					item.setSelected(isChecked);
					
					PlateAdapter.this.notifyDataSetChanged();
					
				}
				
			});
			TextView countView=(TextView) heatView.findViewById(R.id.item_heat_count);
			TextView massView=(TextView) heatView.findViewById(R.id.item_heat_mass);

			countView.setText(""+heat.getCount());
			massView.setText(String.format("%.2f",heat.getMass()));
		//	layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 70));
			return ll;
			

		}
		//----------------------------------Plate--------------------------------------------
		if(item instanceof Plate){
			LinearLayout plateView = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.plate_item_layout, null);
            if(  (getItem(position-1) instanceof Plate) ){
                View dv = plateView.findViewById(R.id.head_plate_item);
                plateView.removeView(dv);
            }
			plateView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			ll.addView(plateView);
			final TextView view= (TextView) plateView.findViewById(R.id.item_plate_text);
			final Plate plt=(Plate) item;
			view.setText(""+plt.hashCode()/10);
            TextView kratView=(TextView) plateView.findViewById(R.id.item_plate_krat);
            kratView.setText(""+plt.hashCode()%10);
//            TextView gradeView=(TextView) plateView.findViewById(R.id.item_plate_grade);
//            gradeView.setText(plt.getGrade());
            TextView sizeView=(TextView) plateView.findViewById(R.id.item_plate_size);
            sizeView.setText(plt.getSize());
            TextView massView=(TextView) plateView.findViewById(R.id.item_plate_mass);
            massView.setText(""+plt.getMass());
			CheckBox cb=(CheckBox) plateView.findViewById(R.id.item_plate_check);
			cb.setChecked(item.isSelected());
			cb.setOnCheckedChangeListener( new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					item.setSelected(isChecked);
					
				}
				
			});
		return	ll;
		}
		
		return null;
	}
public void resetlevel(){
	changeDeepLevel(1);
}
private void changeDeepLevel(int level){
	list.clear();
	List<Pred> orders=place.getOrders();
	List<Heat> heats;
	switch(level){
	case 1:
		deepLevel=1;
		list.addAll(orders);
		break;
	case 2:
		Log.v("level deep","level 2");
		heats=orders.get(currentOrderNum).getHeats();
		for(int i=0;i<=currentOrderNum;i++){
			list.add(orders.get(i));
		}
		for(int i=0;i<heats.size();i++){
			list.add(heats.get(i));
		}
		for(int i=currentOrderNum+1;i<orders.size();i++){
			list.add(orders.get(i));
		}
		deepLevel=2;
		break;
	case 3:
		Log.v("level deep","level 3");
		 heats=orders.get(currentOrderNum).getHeats();
		for(int i=0;i<=currentOrderNum;i++){
			list.add(orders.get(i));
		}
		for(int i=0;i<=currentHeatNum;i++){
			list.add(heats.get(i));
		}
		//--------------------------------------
		list.addAll(heats.get(currentHeatNum).getPlates());
		//--------------------------------------
		for(int i=currentHeatNum+1;i<heats.size();i++){
			list.add(heats.get(i));
		}
		for(int i=currentOrderNum+1;i<orders.size();i++){
			list.add(orders.get(i));
		}
		deepLevel=3;
		break;
		
	}
}

}
