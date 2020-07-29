
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * ViewPanel が配置されるフレーム．このクラスの中にメイン関数がある．メニューバーの設定も行う．
 */
class DrawFrame extends JFrame implements ActionListener, Observer{

	DrawModel model;
	ViewPanel view;
	MyPopupMenu popup;
	DrawController cont;
	Pen pen;
	Animation animation;
	ShapeDialog shapeDlg;
	ColorDialog colorDlg;
	CursorDialog cursorDlg;
	LayerDialog layerDlg;
	AnimationDialog animationDlg;

	// メニューバー
	JMenuBar menubar;
	JMenu menu[];
	JMenuItem menuitem[][];

	/**
	 * フレームを設定する．
	 */
	public DrawFrame(){

		// フレーム
		pen = new Pen();
		model = new DrawModel(this, pen);
		popup = new MyPopupMenu(model);
		cont = new DrawController(this, pen, model, popup);
		view = new ViewPanel(model, cont);
		animation = new Animation(model);
		shapeDlg = new ShapeDialog(model, pen, 500, 0);
		colorDlg = new ColorDialog(pen, model, cont, shapeDlg.getX(), shapeDlg.getY()+shapeDlg.getHeight());
		cursorDlg = new CursorDialog(model, cont, 0, 500);
		layerDlg = new LayerDialog(model, cursorDlg.getX()+cursorDlg.getWidth(), cursorDlg.getY());
		animationDlg = new AnimationDialog(animation, model, shapeDlg.getX() + shapeDlg.getWidth(), shapeDlg.getY());
		model.addObserver(this);
		this.setBackground(Color.black);
		this.setTitle("Draw Editor");
		this.setSize(500, 500);

		this.add(view);

		// メニューバー
		menubar = new JMenuBar();
		menu = new JMenu[3];
		menuitem = new JMenuItem[menu.length][];
		menu[0] = new JMenu("ファイル");
		menuitem[0] = new JMenuItem[8];
		menuitem[0][0] = new JMenuItem("新規");
		menuitem[0][1] = new JMenuItem("開く");
		menuitem[0][2] = null;
		menuitem[0][3] = new JMenuItem("上書き保存");
		menuitem[0][4] = new JMenuItem("名前をつけて保存");
		menuitem[0][5] = new JMenuItem("書き出し");
		menuitem[0][6] = null;
		menuitem[0][7] = new JMenuItem("閉じる");
		menu[1] = new JMenu("編集");
		menuitem[1] = new JMenuItem[13];
		menuitem[1][0] = new JMenuItem("元に戻す");
		menuitem[1][1] = new JMenuItem("やり直し");
		menuitem[1][2] = null;
		menuitem[1][3] = new JMenuItem("切り取り");
		menuitem[1][4] = new JMenuItem("コピー");
		menuitem[1][5] = new JMenuItem("貼り付け");
		menuitem[1][6] = null;
		menuitem[1][7] = new JMenuItem("選択");
		menuitem[1][8] = new JMenuItem("削除");
		menuitem[1][9] = new JMenuItem("グループ化");
		menuitem[1][10] = new JMenuItem("グループ解除");
		menuitem[1][11] = null;
		menuitem[1][12] = new JMenuItem("背景色にする");
		menu[2] = new JMenu("ウィンドウ");
		menuitem[2] = new JMenuItem[5];
		menuitem[2][0] = new JMenuItem("形");
		menuitem[2][1] = new JMenuItem("色");
		menuitem[2][2] = new JMenuItem("カーソル");
		menuitem[2][3] = new JMenuItem("レイヤー");
		menuitem[2][4] = new JMenuItem("アニメーション");
		for(int i=0; i<menu.length; i++) {
			menubar.add(menu[i]);
			for(int j=0; j<menuitem[i].length; j++) {
				if(menuitem[i][j] == null) {
					menu[i].addSeparator();
					continue;
				}
				menuitem[i][j].addActionListener(this);
				menu[i].add(menuitem[i][j]);
			}
		}
		this.setJMenuBar(menubar);

		// フレーム
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	@Override
	/**
	 * メニューの操作を受け取る．
	 */
	public void actionPerformed(ActionEvent e) {
		// メニューバー
		if(e.getSource() == menuitem[0][0]) {
			model.renew();
		}else if(e.getSource() == menuitem[0][1]){
			model.load();
		}else if(e.getSource() == menuitem[0][3]){
			model.overSave();
		}else if(e.getSource() == menuitem[0][4]){
			model.save();
		}else if(e.getSource() == menuitem[0][5]){
			view.save();
		}else if(e.getSource() == menuitem[0][7]){
			System.exit(0);
		}else if(e.getSource() == menuitem[1][0]){
			model.undo();
		}else if(e.getSource() == menuitem[1][1]){
			model.redo();
		}else if(e.getSource() == menuitem[1][3]){
			model.cut();
		}else if(e.getSource() == menuitem[1][4]){
			model.copy();
		}else if(e.getSource() == menuitem[1][5]){
			model.paste();
		}else if(e.getSource() == menuitem[1][7]){
			model.setSelectMode(true);
		}else if(e.getSource() == menuitem[1][8]){
			model.deleteSelectedFigure();
		}else if(e.getSource() == menuitem[1][9]){
			model.grouping();
		}else if(e.getSource() == menuitem[1][10]){
			model.ungrouping();
		}else if(e.getSource() == menuitem[1][12]){
			view.setBackground(pen.getCurrentColor());
		}else if(e.getSource() == menuitem[2][0]){
			shapeDlg.setVisible(true);
		}else if(e.getSource() == menuitem[2][1]){
			colorDlg.setVisible(true);
		}else if(e.getSource() == menuitem[2][2]){
			cursorDlg.setVisible(true);
		}else if(e.getSource() == menuitem[2][3]){
			layerDlg.setVisible(true);
		}else if(e.getSource() == menuitem[2][4]){
			animationDlg.setVisible(true);
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
		// 選択可能・不可能を設定
		menuitem[1][0].setEnabled(model.undoable());
		menuitem[1][1].setEnabled(model.redoable());
		menuitem[1][3].setEnabled(selectSize >= 1);
		menuitem[1][4].setEnabled(selectSize >= 1);
		menuitem[1][5].setEnabled(model.pastable());
		menuitem[1][8].setEnabled(selectSize >= 1);
		menuitem[1][9].setEnabled(selectSize >= 2);
		menuitem[1][10].setEnabled(enable);
	}

	/**
	 * 色取得の値をクラス間でやり取りするための関数
	 * @param x 色を取得したいピクセルの x 座標
	 * @param y 色を取得したいピクセルの y 座標
	 * @return ピクセルの色
	 */
	public Color getColor(int x, int y) {
		return view.getColor(x, y);
	}

	/**
	 * 保存するときに Animation のクラスをやりとりするための関数
	 * @return Animation クラス
	 */
	public Animation getAnimation() {
		return animation;
	}

	/**
	 * 開くときに Animation のクラスをやりとりするための関数
	 * @param a 新しい Animation クラス
	 */
	public void setAnimation(Animation a) {
		animation = a;
	}

	/**
	 * メインクラスです．プログラムの最初に実行されます．
	 */
	public static void main(String argv[]) {
		new DrawFrame();
	}

}
