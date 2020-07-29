
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * アニメーションを設定するダイアログ．アフィン変換行列の時間 t に関する関数を指定する．
 */
public class AnimationDialog extends JDialog implements Observer{

	private Animation animation;
	private DrawModel model;

	/**
	 * Orbit の名前を指定するためのテキストフィールド
	 */
	private JTextField nam;

	/**
	 * 行列関数A(t)の式
	 * [1] [2] [3]
	 * [4] [5] [6]
	 * を入力するための TextField
	 */
	private JTextField txf[];
	/**
	 * 行列関数A(t)の式
	 * [1] [2] [3]
	 * [4] [5] [6]
	 * を表示するための Label
	 */
	private JLabel lbl[];
	/**
	 * 行列関数A(t)の式
	 * [1] [2] [3]
	 * [4] [5] [6]
	 * を表示するための Panel
	 */
	private JPanel matrix;

	/**
	 * 開始時間を入力する Spinner
	 */
	private JSpinner spn;
	/**
	 * 動作時間を入力する Spinner
	 */
	private JSpinner epn;

	/**
	 * list を表示する ScrollPanel
	 */
	private JScrollPane scp;
	/**
	 * Orbit の集合を表示するための List
	 */
	private JList<String> list;

	/**
	 * Orbit を追加するための Button
	 */
	private JButton add;
	/**
	 * Orbit を削除するための Button
	 */
	private JButton del;

	/**
	 * アニメーションを実行するための Button
	 */
	private JButton start;
	/**
	 * アニメーションを停止するための Button
	 */
	private JButton stop;

	/**
	 * 新しいアニメーションダイアログを作成する．
	 * @param anm Animation
	 * @param model DrawModel
	 * @param x 表示する座標 x
	 * @param y 表示する座標 y
	 */
	public AnimationDialog(Animation anm, DrawModel model, int x, int y) {
		animation = anm;
		animation.addObserver(this);
		this.model = model;

		nam = new JTextField("アニメーション0");
		this.add(nam);

		matrix = new JPanel();
		matrix.setBorder(new TitledBorder("アフィン変換行列関数 A(t)"));
		matrix.setLayout(new GridLayout(3, 3));

		txf = new JTextField[6];
		for(int i=0; i<6; i++) {
			txf[i] = new JTextField(""+0);
			txf[i].setPreferredSize(new Dimension(100, 30));
			matrix.add(txf[i]);
		}
		txf[0].setText("" + 1);
		txf[4].setText("" + 1);

		lbl = new JLabel[3];
		for(int i=0; i<3; i++) {
			lbl[i] = new JLabel(""+0, JLabel.CENTER);
			lbl[i].setPreferredSize(new Dimension(100, 30));
			matrix.add(lbl[i]);
		}
		lbl[2].setText("" + 1);

		this.add(matrix);

		spn = new JSpinner(new SpinnerNumberModel(0.0, 0.0, Double.POSITIVE_INFINITY, 1.0));
		spn.setPreferredSize(new Dimension(50, 30));

		epn = new JSpinner(new SpinnerNumberModel(Double.POSITIVE_INFINITY, 0.0, Double.POSITIVE_INFINITY, 1.0));
		epn.setPreferredSize(new Dimension(50, 30));

		JPanel p01 = new JPanel();
		p01.setLayout(new GridLayout(2, 2));
		p01.add(new JLabel("開始時間"));
		p01.add(spn);
		p01.add(new JLabel("動作時間"));
		p01.add(epn);
		this.add(p01);


		ArrayList<Orbit> obt = animation.getOrbits();
		String obtName[] = new String[obt.size()];
		for(int i=0; i<obt.size(); i++) {
			obtName[i] = obt.get(i).getName();
		}
		list = new JList<String>(obtName);
		list.setSelectedIndex(0);
		list.addMouseListener(new ListListener());
		scp = new JScrollPane();
		scp.getViewport().setView(list);
		scp.setPreferredSize(new Dimension(100, 100));
		this.add(scp);

		add = new JButton("追加");
		add.addActionListener(new AddListener());

		del = new JButton("削除");
		del.addActionListener(new DelListener());

		JPanel pnl = new JPanel();
		pnl.add(add);
		pnl.add(del);
		this.add(pnl);

		start = new JButton("再生");
		start.addActionListener(new StartListener());

		stop = new JButton("停止");
		stop.addActionListener(new StopListener());

		JPanel p1 = new JPanel();
		p1.add(start);
		p1.add(stop);
		this.add(p1);


		this.setTitle("アニメーション");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.pack();
		this.setBounds(x, y, this.getWidth(), this.getHeight());
		this.setResizable(false);
		this.setVisible(true);

		animation.updated();
	}



	@Override
	/**
	 * animation が更新された時に呼び出される．
	 */
	public void update(Observable o, Object arg) {
		ArrayList<Orbit> obt = animation.getOrbits();
		String obtName[] = new String[obt.size()];
		for(int i=0; i<obt.size(); i++) {
			obtName[i] = obt.get(i).getName();
		}
		int select = list.getSelectedIndex();
		list.setListData(obtName);
		list.setSelectedIndex(select);

		if(list.getSelectedIndex() != -1) {
			Orbit ob = animation.getOrbits().get(list.getSelectedIndex());
			txf[0].setText(ob.getFunction(0));
			txf[3].setText(ob.getFunction(1));
			txf[1].setText(ob.getFunction(2));
			txf[4].setText(ob.getFunction(3));
			txf[2].setText(ob.getFunction(4));
			txf[5].setText(ob.getFunction(5));

			nam.setText(ob.getName());

			spn.setValue(ob.getStartTime() / 1E9);
			epn.setValue(ob.getOperatingTime() / 1E9);

			ArrayList<Figure> fig = ob.getFigures();
			model.resetSelected();
			for(int i=0; i<fig.size(); i++) {
				model.addSelectedFigure(fig.get(i));
			}
		}

		start.setEnabled(!animation.getRunning());
		stop.setEnabled(animation.getRunning());

		repaint();
	}

	class ListListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			animation.updated();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

	}

	class AddListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<String> fnc = new ArrayList<String>();
			fnc.add(txf[0].getText());
			fnc.add(txf[3].getText());
			fnc.add(txf[1].getText());
			fnc.add(txf[4].getText());
			fnc.add(txf[2].getText());
			fnc.add(txf[5].getText());
			animation.addOrbit(fnc, (double)spn.getValue(), (double)epn.getValue(), nam.getText());
			animation.updated();
		}

	}

	class DelListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			animation.deleteOrbit(list.getSelectedIndex());
			animation.updated();
		}

	}

	class StartListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			animation.execute();
		}

	}

	class StopListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			animation.stop();
		}

	}
}
