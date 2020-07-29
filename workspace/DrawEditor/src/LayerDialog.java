
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * レイヤーに関するダイアログ．
 */
public class LayerDialog extends JDialog implements Observer{

	private DrawModel model;

	private JScrollPane scp;
	/**
	 * 全ての Layer を表示するリスト
	 */
	private JList<String> list;

	/**
	 * Layer の名前を指定する TextField
	 */
	private JTextField nam;
	/**
	 * 単独表示かどうかを切り替える RadioButton
	 */
	private JRadioButton solo;
	/**
	 * 非表示かどうかを切り替える RadioButton
	 */
	private JRadioButton mute;

	/**
	 * Layer を追加するための Button
	 */
	private JButton add;
	/**
	 * Layer を削除するための Button
	 */
	private JButton del;

	/**
	 * 新しい LayerDialog を作成する．
	 * @param model DrawModel
	 * @param x Dialog の x 座標
	 * @param y Dialog の y 座標
	 */
	public LayerDialog(DrawModel model, int x, int y) {
		model.addObserver(this);
		this.model = model;

		ArrayList<Layer> layer = model.getLayer();
		String layerName[] = new String[layer.size()];
		for(int i=0; i<layer.size(); i++) {
			layerName[i] = layer.get(i).getName();
		}
		list = new JList<String>(layerName);
		list.addMouseListener(new ListListener());
		list.setSelectedIndex(model.getSelectedLayer());
		scp = new JScrollPane();
		scp.getViewport().setView(list);
		scp.setPreferredSize(new Dimension(100, 100));
		this.add(scp);

		add = new JButton("追加");
		add.addActionListener(new AddListener());

		del = new JButton("削除");
		del.addActionListener(new DelListener());

		JPanel p00 = new JPanel();
		p00.setLayout(new BoxLayout(p00, BoxLayout.LINE_AXIS));
		p00.add(add);
		p00.add(del);

		JPanel p0 = new JPanel();
		p0.setLayout(new BoxLayout(p0, BoxLayout.PAGE_AXIS));
		p0.add(scp);
		p0.add(p00);
		this.add(p0);


		nam = new JTextField(model.getLayer().get(model.getSelectedLayer()).getName());
		nam.addActionListener(new NamListener());

		solo = new JRadioButton("単独表示");
		solo.addActionListener(new SoloListener());

		mute = new JRadioButton("非表示");
		mute.addActionListener(new MuteListener());

		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.PAGE_AXIS));
		p1.add(nam);
		p1.add(solo);
		p1.add(mute);
		this.add(p1);

		this.setTitle("レイヤー");
		this.setLayout(new FlowLayout());
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.pack();
		this.setBounds(x, y, this.getWidth(), this.getHeight());
		this.setResizable(false);
		this.setVisible(true);
	}

	/**
	 * Layer の名前を指定する JTextField を監視するリスナー．
	 */
	class NamListener implements ActionListener{

		@Override
		/**
		 * Layer の名前を変更する．
		 */
		public void actionPerformed(ActionEvent e) {
			model.getLayer().get(model.getSelectedLayer()).setName(nam.getText());
			model.updated();
		}

	}

	/**
	 * Layer を単独表示するかどうかの変更を監視するリスナー．
	 */
	class SoloListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			model.setLayerSolo(solo.isSelected());
		}

	}

	/**
	 * 選択された Layer が変更されたことを監視するリスナー．
	 */
	class ListListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			model.setSelectedLayer(list.getSelectedIndex());
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

	/**
	 * Layer を非表示にするかどうかの変更を監視するリスナー．
	 */
	class MuteListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			model.setLayerMute(mute.isSelected());
		}

	}

	/**
	 * Layer の追加を監視するリスナー．
	 */
	class AddListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			model.addLayer();
		}

	}

	/**
	 * Layer の削除を監視するリスナー．
	 */
	class DelListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			model.deleteLayer();
		}

	}

	@Override
	/**
	 * DrawModel が変更されたときに呼び出される．
	 */
	public void update(Observable o, Object arg) {
		ArrayList<Layer> layer = model.getLayer();
		int select = model.getSelectedLayer();
		String layerName[] = new String[layer.size()];
		for(int i=0; i<layer.size(); i++) {
			layerName[i] = layer.get(i).getName();
		}
		list.setListData(layerName);
		list.setSelectedIndex(select);

		nam.setText(layer.get(select).getName());

		mute.setSelected(layer.get(select).getMute());
		repaint();
	}


}

