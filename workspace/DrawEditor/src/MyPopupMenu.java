
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 * ViewPanel で右クリックした時に表示されるポップアップメニュー．メニューバーの「編集」とほぼ同じ．
 */
public class MyPopupMenu extends JPopupMenu implements ActionListener, Observer{

	private DrawModel model;

	/**
	 * メニューの項目
	 */
	JMenuItem menuitem1[];

	/**
	 * 新しい MuPopupMenu を作成する．
	 * @param model DrawModel
	 */
	public MyPopupMenu(DrawModel model) {
		this.model = model;
		model.addObserver(this);

		menuitem1 = new JMenuItem[13];
		menuitem1[0] = new JMenuItem("元に戻す");
		menuitem1[1] = new JMenuItem("やり直し");
		menuitem1[2] = null;
		menuitem1[3] = new JMenuItem("切り取り");
		menuitem1[4] = new JMenuItem("コピー");
		menuitem1[5] = new JMenuItem("貼り付け");
		menuitem1[6] = null;
		menuitem1[7] = new JMenuItem("選択");
		menuitem1[8] = new JMenuItem("削除");
		menuitem1[9] = new JMenuItem("グループ化");
		menuitem1[10] = new JMenuItem("グループ解除");
		for(int i=0; i<11; i++) {
			if(menuitem1[i] == null) {
				this.addSeparator();
				continue;
			}
			menuitem1[i].addActionListener(this);
			this.add(menuitem1[i]);
		}
	}

	@Override
	/**
	 * メニューの操作を受け取る．
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == menuitem1[0]){
			model.undo();
		}else if(e.getSource() == menuitem1[1]){
			model.redo();
		}else if(e.getSource() == menuitem1[3]){
			model.cut();
		}else if(e.getSource() == menuitem1[4]){
			model.copy();
		}else if(e.getSource() == menuitem1[5]){
			model.paste();
		}else if(e.getSource() == menuitem1[7]){
			model.setSelectMode(true);
		}else if(e.getSource() == menuitem1[8]){
			model.deleteSelectedFigure();
		}else if(e.getSource() == menuitem1[9]){
			model.grouping();
		}else if(e.getSource() == menuitem1[10]){
			model.ungrouping();
		}
	}

	@Override
	/**
	 * メニューの選択可能・不可能を設定する．
	 */
	public void update(Observable o, Object arg) {
		// 選択されている Figure の数
		int selectSize = model.getSelectedFigure().size();
		// ungroup できるかどうか
		boolean enable = false;
		ArrayList<Figure> sc = model.getSelectedFigure();
		for(int i=0; i<sc.size(); i++) {
			if(sc.get(i).getFigures() != null) {enable=true; break;}
		}
		// ポップアップメニュー
		menuitem1[0].setEnabled(model.undoable());
		menuitem1[1].setEnabled(model.redoable());
		menuitem1[3].setEnabled(selectSize >= 1);
		menuitem1[4].setEnabled(selectSize >= 1);
		menuitem1[5].setEnabled(model.pastable());
		menuitem1[8].setEnabled(selectSize >= 1);
		menuitem1[9].setEnabled(selectSize >= 2);
		menuitem1[10].setEnabled(enable);
	}

}
