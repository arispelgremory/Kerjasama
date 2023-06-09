package com.gremoryyx.kerjasama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(private val notificationDataList: ArrayList<NotificationData>) : RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val notificationData: NotificationData = notificationDataList[position]

        // Set the Bitmap to the ImageView
        if (holder.viewCompanyImg != null) {
            holder.viewCompanyImg.scaleType = ImageView.ScaleType.FIT_CENTER
            holder.viewCompanyImg.setImageBitmap(notificationData.companyImg)
        }

        holder.viewMessages.text = notificationData.messages?: "NULL"
    }

    override fun getItemCount(): Int {
        return notificationDataList.size
    }

    class MyViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val viewCompanyImg = itemView.findViewById<ImageView>(R.id.Notice_CompanyImg)
        val viewMessages = itemView.findViewById<TextView>(R.id.Notice_Messages)
    }




}
