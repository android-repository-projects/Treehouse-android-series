package com.teamtreehouse.ribbit;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

public class MessageAdapter extends ArrayAdapter<ParseObject>{
	
	protected Context mContext;
	protected List<ParseObject> mMessage;
	
	public MessageAdapter(Context context, List<ParseObject> messages){
		super(context, R.layout.message_item, messages);
		mContext=context;
		mMessage=messages;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.message_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView
					.findViewById(R.id.messageIcon);
			holder.label = (TextView) convertView
					.findViewById(R.id.messageLabel);
			convertView.setTag(holder);
		} else {
			holder= (ViewHolder) convertView.getTag();
		}

		ParseObject message=mMessage.get(position);
		if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {//se for img metemos icon img
			holder.icon.setImageResource(R.drawable.ic_action_picture);
		} else {
			holder.icon.setImageResource(R.drawable.ic_action_play_over_video);
		}
		
		holder.label.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView icon;
		TextView label;
	}
	
	public void refill(List<ParseObject> messages){
		mMessage.clear();//limpamos
		mMessage.addAll(messages);//metemos outra vez
		notifyDataSetChanged();
	}
}
