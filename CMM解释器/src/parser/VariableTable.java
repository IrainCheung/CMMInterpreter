package parser;

import java.util.ArrayList;

/**
 * 用于存储在语法分析过程中遇到的变量
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
	 * 添加变量
	 * @param variable
	 */
	public void add(Variable variable) {
		list.add(variable);
	}
	/**
	 * 获取变量表大小
	 * @return
	 */
	public int size() {
		return list.size();
	}
	/**
	 * 删除指定位置的变量
	 * @param index
	 * @return
	 */
	public boolean remove(int index) {
		return list.remove(index) != null;
	}
	/**
	 * 删除变量
	 * @param variable
	 * @return
	 */
	public boolean remove(Variable variable) {
		return list.remove(variable);
	}
	/**
	 * 判断是否包含变量
	 * @param variable
	 * @return
	 */
	public boolean contains(Variable variable) {
		
		for (int i = 0; i < list.size(); i++) {
			Variable tmp=list.get(i);
			//区别变量的三个特征就是：变量的类型，名字，层次
			if (tmp.getName().equals(variable.getName())&&tmp.getScope()==variable.getScope()) {//tmp.getVt()==variable.getVt()&&
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 获取变量
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
	 * 获取指定位置的变量
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
