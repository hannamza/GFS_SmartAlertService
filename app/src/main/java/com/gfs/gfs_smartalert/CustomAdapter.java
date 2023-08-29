package com.gfs.gfs_smartalert;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    public class ListContents{
        SpannableString msg;
        int type;
        ListContents(SpannableString _msg,int _type)
        {
            this.msg = _msg;
            this.type = _type;
        }
    }
    int nCountType1 = 0;
    int nCountType2 = 0;
    int nCountType3 = 0;
    int nCountType4 = 0;
    int nCountType5 = 0;

    int m_nFontType = 0, m_nOldFontType = 0;

    boolean mShow1 = true, mShow2 = true, mShow3 = true, mShow4 = true;

    private ArrayList m_List;
    private ArrayList m_backupList;
    public CustomAdapter() {
        m_List = new ArrayList();
        m_backupList = new ArrayList();
    }
    // 외부에서 아이템 추가 요청 시 사용
    public void add(String _msg,int _type) {
        String content = _msg;
        SpannableString spannableString = new SpannableString(_msg);

        int start = 0;
        int end = 5;

        if(_type != 2) {
            switch (_type) {
                case 3:// 화재
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#EC1C24")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 4: // 가스
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFB200")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 5: // 감시
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#24D900")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 6: // 단선
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0099FF")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 7: // 복구
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#008C28")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
            }
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(1.1f), 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(1.2f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else{
            spannableString.setSpan(new StyleSpan(Typeface.BOLD),0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(1.1f), 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        switch(m_nFontType)
        {
            case 0: break;
            case 1:
                spannableString.setSpan(new RelativeSizeSpan(1.2f), 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case 2:
                spannableString.setSpan(new RelativeSizeSpan(1.4f), 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
        }

        int nIndex = m_backupList.size(); //getCount();

        //m_List.add(nIndex, new ListContents(spannableString,_type));
        m_backupList.add(nIndex, new ListContents(spannableString,_type));

        switch(_type)
        {
            case 3:// 화재
                if(mShow1)
                {
                    m_List.add(m_List.size(), new ListContents(spannableString,_type));
                }
                break;
            case 4: // 가스
                if(mShow2)
                {
                    m_List.add(m_List.size(), new ListContents(spannableString,_type));
                }
                break;
            case 5: // 감시
                if(mShow3)
                {
                    m_List.add(m_List.size(), new ListContents(spannableString,_type));
                }
                break;
            case 6: // 단선
                if(mShow4)
                {
                    m_List.add(m_List.size(), new ListContents(spannableString,_type));
                }
                break;
            case 7: // 복구
            default:
                m_List.add(m_List.size(), new ListContents(spannableString,_type));
                break;
        }
    }

    public void insert(int nAlarmTitleLength, String _msg,int _type, int nIndex) {
        SpannableString spannableString = new SpannableString(_msg);

        int start = 0;
        int end = 5;

        //nAlarmTitleLength가 0이 아니면 영문
        if (nAlarmTitleLength != 0)
        {
            end = nAlarmTitleLength;
        }

        if(_type != 2) {
            //핸드폰이 한국이면 그대로 start, end를 쓰면 되고 그렇지 않으면 영문 텍스트 길이만큼 계산해서 색상을 줄 수 있도록 해야 함, add 매서드도 마찬가지
            switch (_type) {
                case 3:// 화재
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#EC1C24")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 4: // 가스
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFB200")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 5: // 감시
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#24D900")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 6: // 단선
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0099FF")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 7: // 복구
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#008C28")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
            }
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(1.1f), 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(1.2f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else {
            spannableString.setSpan(new StyleSpan(Typeface.BOLD),0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(1.1f), 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //int nIndex = 1;
        /*for(int i = 0; i < getCount(); i++)
        {
            // 날짜 비교 후 알맞은 인덱스를 찾아 add
            String sValue = getItem(i).toString();
        }*/
        switch(m_nFontType)
        {
            case 0: break;
            case 1:
                spannableString.setSpan(new RelativeSizeSpan(1.2f), 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case 2:
                spannableString.setSpan(new RelativeSizeSpan(1.4f), 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
        }

        //m_List.add(nIndex, new ListContents(spannableString,_type));
        m_backupList.add(nIndex, new ListContents(spannableString,_type));
        switch(_type)
        {
            case 3:// 화재
                if(mShow1)
                {
                    m_List.add(nIndex, new ListContents(spannableString,_type));
                }
                break;
            case 4: // 가스
                if(mShow2)
                {
                    m_List.add(nIndex, new ListContents(spannableString,_type));
                }
                break;
            case 5: // 감시
                if(mShow3)
                {
                    m_List.add(nIndex, new ListContents(spannableString,_type));
                }
                break;
            case 6: // 단선
                if(mShow4)
                {
                    m_List.add(nIndex, new ListContents(spannableString,_type));
                }
                break;
            case 7: // 복구
                m_List.add(nIndex, new ListContents(spannableString,_type));
                break;
            default:
                m_List.add(nIndex, new ListContents(spannableString,_type));
                break;
        }
    }

    public int getItemCount(int nType)
    {
        switch(nType)
        {
            case 0:
                return nCountType1;
            case 1:
                return nCountType2;
            case 2:
                return nCountType3;
            case 3:
                return nCountType4;
            case 4:
                return nCountType5;
            default:
                break;
        }
        return 0;
    }
    public void SetFontType(int nType)
    {
        m_nFontType = nType;
        if(m_nFontType != m_nOldFontType){
            Redisplay();
            notifyDataSetChanged();
        }
    }

    //현재 콜되는 곳이 없음
    public void GetDataCount(){
        nCountType1 = 0;
        nCountType2 = 0;
        nCountType3 = 0;
        nCountType4 = 0;
        nCountType5 = 0;
        boolean bType1 = false;
        boolean bType2 = false;
        boolean bType3 = false;
        boolean bType4 = false;

        Object obj;
        for(int i = 0; i < m_backupList.size(); i++) {
            obj = m_backupList.get(i);

            String sMsg = ((ListContents) obj).msg.toString();

            switch (((ListContents) obj).type) {
                case 3:// 화재
                    if(sMsg.contains("화재 복구")){
                        //bType1 = true;
                        --nCountType1;
                    }
                    else if(!bType1){
                        ++nCountType1;
                    }
                    break;
                case 4: // 가스
                    if(sMsg.contains("가스 복구")){
                        //bType2 = true;
                        --nCountType2;
                    }
                    else if(!bType2){
                        ++nCountType2;
                    }
                    break;
                case 5: // 감시
                    if(sMsg.contains("감시 복구")){
                        //bType3 = true;
                        --nCountType3;
                    }
                    else if(!bType3){
                        ++nCountType3;
                    }
                    break;
                case 6: // 단선
                    if(sMsg.contains("단선 복구")){
                        //bType4 = true;
                        --nCountType4;
                    }
                    else if(!bType4){
                        ++nCountType4;
                    }
                    break;
                case 7: // 복구
                    if(nCountType1 < 0){
                        nCountType1 = 0;
                    }
                    if(nCountType2 < 0){
                        nCountType2 = 0;
                    }
                    if(nCountType3 < 0){
                        nCountType3 = 0;
                    }
                    if(nCountType4 < 0){
                        nCountType4 = 0;
                    }
                    return;
                default:
                    break;
            }
        }
        if(nCountType1 < 0){
            nCountType1 = 0;
        }
        if(nCountType2 < 0){
            nCountType2 = 0;
        }
        if(nCountType3 < 0){
            nCountType3 = 0;
        }
        if(nCountType4 < 0){
            nCountType4 = 0;
        }
    }

    public void Redisplay()
    {
        while(!m_List.isEmpty())
        {
            m_List.remove(0);
        }
        String sValue;
        int nType;
        Object obj;
        int nIndex = 0;

        boolean bTrue = false;
        ArrayList<String> dateList = new ArrayList<String>();;
        for(int i = 0; i < m_backupList.size(); i++)
        {
            obj = m_backupList.get(i);
            if(m_nFontType != m_nOldFontType){
                switch(m_nOldFontType)
                {
                    case 0: break;
                    case 1:
                        ((ListContents)obj).msg.setSpan(new RelativeSizeSpan(0.8333334f), 0, ((ListContents)obj).msg.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case 2:
                        ((ListContents)obj).msg.setSpan(new RelativeSizeSpan(0.7142858f), 0, ((ListContents)obj).msg.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                }
                switch(m_nFontType)
                {
                    case 0: break;
                    case 1:
                        ((ListContents)obj).msg.setSpan(new RelativeSizeSpan(1.2f), 0, ((ListContents)obj).msg.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case 2:
                        ((ListContents)obj).msg.setSpan(new RelativeSizeSpan(1.4f), 0, ((ListContents)obj).msg.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                }
            }
            if(((ListContents)obj).type == 2){
                continue;
            }
            String strTemp = ((ListContents)obj).msg.toString();
            String[] temp = strTemp.split("\n");
            if(temp.length >= 2){
                String[] value = temp[1].split(" ");
                bTrue = false;
                for(String s : dateList){
                    if(s.equals(value[0])){
                        bTrue = true;
                        break;
                    }
                }
                switch(((ListContents)obj).type)
                {
                    case 3: if(!mShow1){ continue; } break;
                    case 4: if(!mShow2){ continue; } break;
                    case 5: if(!mShow3){ continue; } break;
                    case 6: if(!mShow4){ continue; } break;
                    default: break;
                }
                if(!bTrue){
                    dateList.add(value[0]);
                    SpannableString spannableString = new SpannableString(value[0]);
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD),0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new RelativeSizeSpan(1.1f), 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    m_List.add(nIndex++, new ListContents(spannableString,2));
                }
            }

            switch(((ListContents)obj).type)
            {
                case 3:// 화재
                    if(mShow1) {
                        m_List.add(nIndex++, new ListContents(((ListContents)obj).msg, ((ListContents)obj).type));
                    }
                    break;
                case 4: // 가스
                    if(mShow2) {
                        m_List.add(nIndex++, new ListContents(((ListContents)obj).msg, ((ListContents)obj).type));
                    }
                    break;
                case 5: // 감시
                    if(mShow3) {
                        m_List.add(nIndex++, new ListContents(((ListContents)obj).msg, ((ListContents)obj).type));
                    }
                    break;
                case 6: // 단선
                    if(mShow4) {
                        m_List.add(nIndex++, new ListContents(((ListContents)obj).msg, ((ListContents)obj).type));
                    }
                    break;
                case 7: // 복구
                    m_List.add(nIndex++, new ListContents(((ListContents)obj).msg, ((ListContents)obj).type));
                    break;
                default:
                    //m_List.add(nIndex++, new ListContents(((ListContents)obj).msg, ((ListContents)obj).type));
                    break;
            }
        }
        m_nOldFontType = m_nFontType;
    }

    public void SetShowType(boolean showTotal, boolean show1, boolean show2, boolean show3, boolean show4)
    {
        if(showTotal)
        {
            mShow1 = true;
            mShow2 = true;
            mShow3 = true;
            mShow4 = true;

            Redisplay();
        }
        else if(mShow1 != show1 || mShow2 != show2 || mShow3 != show3 || mShow4 != show4)
        {
            mShow1 = show1;
            mShow2 = show2;
            mShow3 = show3;
            mShow4 = show4;
            if(!mShow4 && !mShow3 && !mShow2 && !mShow1)
            {
                mShow1 = true;
                mShow2 = true;
                mShow3 = true;
                mShow4 = true;
            }

            Redisplay();
        }
    }

    // 외부에서 아이템 삭제 요청 시 사용
    public void remove(int _position) {
        m_List.remove(_position);
    }
    @Override
    public int getCount() {
        return m_List.size();
    }

    @Override
    public Object getItem(int position) {
        return m_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        TextView text    = null;
        CustomHolder    holder  = null;
        LinearLayout layout  = null;
        View            viewRight = null;
        View            viewLeft = null;

        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 converView가 null인 상태로 들어 옴
        if ( convertView == null ) {
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gfs_item, parent, false);

            layout    = (LinearLayout) convertView.findViewById(R.id.layout);
            text    = (TextView) convertView.findViewById(R.id.text);
            viewRight    = (View) convertView.findViewById(R.id.imageViewright);
            viewLeft    = (View) convertView.findViewById(R.id.imageViewleft);

            // 홀더 생성 및 Tag로 등록
            holder = new CustomHolder();
            holder.m_TextView   = text;
            holder.layout = layout;
            holder.viewRight = viewRight;
            holder.viewLeft = viewLeft;
            convertView.setTag(holder);
        }
        else {
            holder  = (CustomHolder) convertView.getTag();
            text    = holder.m_TextView;
            layout  = holder.layout;
            viewRight = holder.viewRight;
            viewLeft = holder.viewLeft;
        }

        // Text 등록
        text.setText(((ListContents)m_List.get(position)).msg);

        if( ((ListContents)m_List.get(position)).type == 0 ) {
            text.setBackgroundResource(R.drawable.inbox2);
            layout.setGravity(Gravity.LEFT);
            viewRight.setVisibility(View.GONE);
            viewLeft.setVisibility(View.GONE);
        }else if(((ListContents)m_List.get(position)).type == 1){
            text.setBackgroundResource(R.drawable.box);
            layout.setGravity(Gravity.RIGHT);
            viewRight.setVisibility(View.GONE);
            viewLeft.setVisibility(View.GONE);
        }else if(((ListContents)m_List.get(position)).type == 2){
            text.setBackgroundResource(R.drawable.date_box);
            layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
            viewRight.setVisibility(View.VISIBLE);
            viewLeft.setVisibility(View.VISIBLE);
        }else {
            text.setBackgroundResource(R.drawable.box);
            layout.setGravity(Gravity.RIGHT);
            viewRight.setVisibility(View.GONE);
            viewLeft.setVisibility(View.GONE);
        }


        // 리스트 아이템을 터치 했을 때 이벤트 발생
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 터치 시 해당 아이템 이름 출력
                //Toast.makeText(context, "리스트 클릭 : "+m_List.get(pos), Toast.LENGTH_SHORT).show();
            }
        });


        // 리스트 아이템을 길게 터치 했을때 이벤트 발생
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // 터치 시 해당 아이템 이름 출력
                //Toast.makeText(context, "리스트 롱 클릭 : "+m_List.get(pos), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return convertView;
    }

    private class CustomHolder {
        TextView    m_TextView;
        LinearLayout layout;
        View viewRight;
        View viewLeft;
    }
}