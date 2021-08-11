package com.eningqu.aipen.base.adapter;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/20 14:11
 */

public interface MultiItemTypeSupport<T>
{
    int getLayoutId(int itemType);

    int getItemViewType(int position, T t);

}