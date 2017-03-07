package com.xdja.usbdemo.ui.adpter;

import java.util.List;

import com.xdja.usbdemo.R;
import com.xdja.usbdemo.bean.PersonBean;
import com.xdja.usbdemo.utils.Base64Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonListAdpter  extends BaseAdapter  {
    
    List<PersonBean> list;
    
    Context context;

    OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onClick(int position);
    }

    public void setOnDeleteClickListener (OnDeleteClickListener listener) {
        onDeleteClickListener = listener;
    }

    public PersonListAdpter(Context context, List<PersonBean> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) 
            return list.size();
        return 0;
    }
    
    public void setListData(List<PersonBean> list) {
        this.list = list;
    }

    @Override
    public Object getItem(int position) {
        if (list != null && list.size() > position)
            return list.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.person_item, null);
            convertView.setTag(viewHolder);
            viewHolder.portrait = (ImageView) convertView.findViewById(R.id.imageView1);
            viewHolder.tvName_gender_age = (TextView) convertView.findViewById(R.id.name_gender_age);
            viewHolder.tvIDNum = (TextView) convertView.findViewById(R.id.id_number);
            viewHolder.btnDelete = (Button) convertView.findViewById(R.id.delete);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        PersonBean personBean = list.get(position);
        
        String path = personBean.getImage_path();

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        viewHolder.portrait.setImageBitmap(bitmap);
        viewHolder.tvName_gender_age.setText(personBean.getName() + " " + personBean.getSurname() 
                                              + "  " + personBean.getSex()
                                              + "  " + personBean.getDob());
        
        viewHolder.tvIDNum.setText(personBean.getIdNo());
        
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteClickListener.onClick(position);
            }
        });

        return convertView;
    }


    static class ViewHolder {
        public ImageView portrait;
        public TextView tvName_gender_age;
        public TextView tvIDNum;
        public Button btnDelete;
    }

}
