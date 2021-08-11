package com.eningqu.aipen.manager;

import androidx.fragment.app.Fragment;
import android.text.TextUtils;

import com.eningqu.aipen.R;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.NumberUtil;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.fragment.FragmentBook;

import java.util.ArrayList;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/5/27 10:27
 * desc   :
 * version: 1.0
 */
public class SpinNotebookManager {
    public static final String TAG = SpinNotebookManager.class.getSimpleName();

    private static SpinNotebookManager instance;

    /**
     * 所有笔记本
     */
    private List<NoteBookData> mAllNoteBookDatas = new ArrayList<>();
    /**
     * 当前加载的笔记本
     */
    private List<NoteBookData> mAdapterNoteBookDatas = new ArrayList<>();
    /**
     * 当前加载的笔记本封面
     */
    private List<Fragment> mAdapterBookCovers = new ArrayList<>();//转盘显示的笔记本封面

    /**
     * 上一个选择的位置
     */
    private int mLastPosition;
    /**
     * 当前选择的位置
     */
    private int mCurPosition;
    /**
     * 当前轮数
     */
    private int mCurRound;

    public static SpinNotebookManager getInstance() {
        if (null == instance) {
            synchronized (SpinNotebookManager.class) {
                if (null == instance) {
                    instance = new SpinNotebookManager();
                }
            }
        }
        return instance;
    }

    public List<NoteBookData> getAllNoteBookDatas() {
        return mAllNoteBookDatas;
    }

    public List<NoteBookData> getAdapterNoteBookDatas() {
        return mAdapterNoteBookDatas;
    }

    public List<Fragment> getAdapterBookCovers() {
        return mAdapterBookCovers;
    }

    public int getLastPosition() {
        return mLastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.mLastPosition = lastPosition;
    }

    public int getCurPosition() {
        return mCurPosition;
    }

    public void setCurPosition(int curPosition) {
        this.mCurPosition = curPosition;
    }

    public int getCurRound() {
        return mCurRound;
    }

    public void reset() {
        setCurRound(0);
        setCurPosition(0);
        setLastPosition(0);
    }

    public void setCurRound(int curRound) {
        this.mCurRound = curRound;
        L.error("set mCurRound=" + mCurRound);
    }

    public void addNotebook(NoteBookData noteBookData) {
        mAdapterNoteBookDatas.add(noteBookData);
        mAllNoteBookDatas.add(noteBookData);
    }

    public void delNotebook(NoteBookData noteBookData) {
        if (mAdapterNoteBookDatas.size() > 0) {
            mAdapterNoteBookDatas.remove(mAdapterNoteBookDatas.size() - 1);
        }
    }

    public void delNotebook(int position, NoteBookData noteBookData) {
        if (position < mAdapterNoteBookDatas.size()) {
            mAdapterNoteBookDatas.remove(position);
        }
    }

    public NoteBookData getCurNotebookData() {
        NoteBookData noteBookData = null;

        if (null != mAllNoteBookDatas && mAllNoteBookDatas.size() > 0) {
            int index = mCurRound * 10 + mCurPosition;
            if (index < mAllNoteBookDatas.size()) {
                noteBookData = mAllNoteBookDatas.get(mCurRound * 10 + mCurPosition);
            }
        }
        return noteBookData;
    }

    /**
     * 添加笔记本封面
     *
     * @param position
     * @param cover_index
     */
    private void addBookCover(int position, int cover_index) {
        FragmentBook fragment = FragmentBook.newInstance();
        int coverIndex = cover_index % Constant.BOOK_COVERS.length;//取余可循环使用封皮
        fragment.setBackground(Constant.BOOK_COVERS[coverIndex]);
        /*if(0==mCurRound){
        }else {
            fragment.setBackground(Constant.BOOK_COVERS[coverIndex-1==-1?0:coverIndex-1]);
        }*/
        mAdapterBookCovers.add(position, fragment);
    }

    /**
     * 添加笔记本封面
     *
     * @param position
     * @param url
     */
    private void addBookCover(int position, String url) {
        FragmentBook fragment = FragmentBook.newInstance();
        fragment.setBackground(url);
        mAdapterBookCovers.add(position, fragment);
    }

    public void addBookCovers(List<NoteBookData> list) {
        L.info(TAG, "add book covers");
        if (null != mAdapterBookCovers && mAdapterBookCovers.size() > 0) {
            mAdapterBookCovers.clear();
        }

        if (mAdapterNoteBookDatas != list) {
            mAdapterNoteBookDatas.clear();
            mAdapterNoteBookDatas.addAll(list);
        }

        if (null == list || list.size() == 0) {
            addEmptyBookCover();
            return;
        } else if (list.size() > 13) {
            return;
        }

        if (null != mAdapterNoteBookDatas && mAdapterNoteBookDatas.size() > 0) {
            int position = 0;
            String strCover;
            for (NoteBookData bookData : mAdapterNoteBookDatas) {
                strCover = bookData.noteCover;
                if (!TextUtils.isEmpty(strCover) && strCover.startsWith("http")) {
                    addBookCover(position, strCover);
                } else if (!TextUtils.isEmpty(strCover) && NumberUtil.isNumeric(strCover)) {
                    int res = Integer.valueOf(strCover);
                    addBookCover(position, res);
                } else {
                    addBookCover(position, 0);
                }
                position++;
            }
        }

        addEmptyBookCover();
    }

    /**
     * 添加空封面
     */
    private void addEmptyBookCover() {
        FragmentBook fragment = FragmentBook.newInstance();
        fragment.setBackground(R.drawable.empty_book);
        fragment.setEmpty(true);
        mAdapterBookCovers.add(fragment);
    }

    /**
     * 更新笔记本列表信息
     */
    public List<NoteBookData> getNotebookUnlockList() {
        if (null != mAllNoteBookDatas && mAllNoteBookDatas.size() > 0) {
            mAllNoteBookDatas.clear();
        }

        //获取未锁定的笔记本
        List<NoteBookData> list = AppCommon.loadNoteBookData(0);
        if (null != list) {
            mAllNoteBookDatas.addAll(list);
        }
        return mAllNoteBookDatas;
    }
}
