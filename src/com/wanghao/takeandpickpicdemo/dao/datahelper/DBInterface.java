package com.wanghao.takeandpickpicdemo.dao.datahelper;

import java.util.List;

import android.content.ContentValues;

/**
 * DB�����ӿ���
 * @author wanghao
 *
 * @param <T>
 */
public interface DBInterface<T> {
    /**
     * ��ѯĳһ����¼
     *
     * @param id ID
     * @return T ���ز�ѯ���õ�һ����¼
     */
    public T query(String id);

    /**
     * ɾ����������
     *
     * @return count ���β���������
     */
    public int clearAll();

    /**
     * ������������
     *
     * @param listData ��Ҫ����������б�
     */
    public void bulkInsert(List<T> listData);

    public ContentValues getContentValues(T data);
}
