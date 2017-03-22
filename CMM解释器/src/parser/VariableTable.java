package parser;

import java.util.ArrayList;

/**
 * ���ڴ洢���﷨���������������ı���
 * @author Qideas
 *
 */
public class VariableTable {

	/**
	 * VariableTable
	 */
	private ArrayList<Variable> list=new ArrayList<>();

	public ArrayList<Variable> getList() {
		return list;
	}

	public void setList(ArrayList<Variable> list) {
		this.list = list;
	}
	/**
	 * ��ӱ���
	 * @param variable
	 */
	public void add(Variable variable) {
		list.add(variable);
	}
	/**
	 * ��ȡ�������С
	 * @return
	 */
	public int size() {
		return list.size();
	}
	/**
	 * ɾ��ָ��λ�õı���
	 * @param index
	 * @return
	 */
	public boolean remove(int index) {
		return list.remove(index) != null;
	}
	/**
	 * ɾ������
	 * @param variable
	 * @return
	 */
	public boolean remove(Variable variable) {
		return list.remove(variable);
	}
	/**
	 * �ж��Ƿ��������
	 * @param variable
	 * @return
	 */
	public boolean contains(Variable variable) {
		
		for (int i = 0; i < list.size(); i++) {
			Variable tmp=list.get(i);
			//��������������������ǣ����������ͣ����֣����
			if (tmp.getName().equals(variable.getName())&&tmp.getScope()==variable.getScope()) {//tmp.getVt()==variable.getVt()&&
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * ��ȡ����
	 * @param v
	 * @return
	 */
	public Variable getVariable(Variable v) {
		
		for (int i = list.size()-1; i >=0; i--) {//
			Variable tmp=list.get(i);
			if (tmp.getName().equals(v.getName())&&tmp.getScope()<=v.getScope()) {//tmp.getVt()==v.getVt()&&
				return tmp;
			}
		}
		return null;
	}
	
	/**
	 * ��ȡָ��λ�õı���
	 * @param index
	 * @return
	 */
	public Variable getVariableAtIndexOf(int index) {
		
		return list.get(index);
	}
	
	public int indexOf(Variable variable) {
		
		return list.indexOf(variable);
	}
	
	public void print() {
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).toString());
		}
	}
	
}
