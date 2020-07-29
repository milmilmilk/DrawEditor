
import java.awt.*;
import java.io.Serializable;
import java.util.*;

/**
 * レイヤー． Figure を保持している．表示・非表示はレイヤーごとに切り替わる．一方，単独表示するかどうかの設定は全てのレイヤーに適用されるので DrawModel で保持されている．
 */
public class Layer implements Serializable {

	/**
	 * Layer にある Figure のリスト
	 */
	private ArrayList<Figure> fig;
	/**
	 * Layer の名前
	 */
	private String name;
	/**
	 * この Layer が非表示かどうか
	 */
	private boolean mute;

	/**
	 * 「レイヤーn」という名前のレイヤーを作成する．
	 * @param n 名前に使用する番号
	 */
	public Layer(int n) {
		fig = new ArrayList<Figure>();
		name = "レイヤー" + n;
	}

	/**
	 * Layer に Figure を加える．
	 * @param f 加える Figure
	 */
	public void addFigure(Figure f) {
		fig.add(f);
	}

	/**
	 * Layer から指定された Figure を取り除く．
	 * @param f 取り除く Figure
	 */
	public void removeFigure(Figure f) {
		fig.remove(f);
	}

	public ArrayList<Figure> getFigure(){
		return fig;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}

	public boolean getMute() {
		return mute;
	}

	/**
	 * Layer に含まれる Figure を描画する．
	 * @param g Graphics
	 */
	public void draw(Graphics g) {
		if(mute) return;
		for (int i = 0; i < fig.size(); i++) {
			Figure f = fig.get(i);
			f.draw(g);
		}
	}

	/**
	 * 指定された座標に存在する Figure を返す．存在しない場合は null を返す．複数存在する場合は後に追加された Figure を返す．
	 * @param mx x 座標
	 * @param my y 座標
	 * @return 点 (x, y) に存在する Figure;
	 */
	public Figure selectFigure(double mx, double my) {
		for(int i=fig.size()-1; i>=0; i--) {
			if(fig.get(i).isInternal(mx, my)) {
				return fig.get(i);
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
