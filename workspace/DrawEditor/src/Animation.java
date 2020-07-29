
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.*;

/**
 * Figure を動かすためのクラス． Figure 内の drawAffine を書き換え， affine は書き換えない．
 */
public class Animation extends Observable implements Runnable, Serializable {

	private transient DrawModel model;

	/**
	 * orbit を保持する．
	 */
	private ArrayList<Orbit> orbits;

	/**
	 * 再生中かどうか．
	 */
	private transient boolean running;

	/**
	 * 新しいアニメーションを作成する．
	 * @param model DrawModel
	 */
	public Animation(DrawModel model) {
		this.model = model;
		orbits = new ArrayList<Orbit>();
		// orbits.add(new Orbit()); // 恒等変換で初期化
		running = false;
	}

	/**
	 * Orbit を追加する．
	 * @param fnc 関数行列 A(t)
	 * @param startT 開始時間
	 * @param operatingT 動作時間
	 * @param name 名前
	 */
	public void addOrbit(ArrayList<String> fnc, double startT, double operatingT, String name) {
		Orbit obt = new Orbit(model.getSelectedFigure(), fnc, (long)(startT*1E9), (long)(operatingT*1E9), name);
		addOrbit(obt);
	}

	/**
	 * Orbit を追加する．
	 * @param obt 追加するOrbit
	 */
	public void addOrbit(Orbit obt){
		for(int i=0; i<orbits.size(); i++) {
			if(orbits.get(i).getStartTime() > obt.getStartTime()) {
				orbits.add(i, obt);
				updated();
				return;
			}
		}
		orbits.add(obt);
		updated();
	}

	/**
	 * Orbit を削除する．
	 * @param i インディックス
	 */
	public void deleteOrbit(int i) {
		if(0 <= i && i < orbits.size()) {
			orbits.remove(i);
			updated();
		}
	}

	public ArrayList<Orbit> getOrbits() {
		return orbits;
	}

	/**
	 * 実行中かどうか返す．
	 * @return 実行中であるか
	 */
	public boolean getRunning() {
		return running;
	}

	/**
	 * アニメーションを実行する．
	 */
	public void execute() {
		if(running) return;
		running = true;
		model.setAnimationMode(true);
		updated();
		Thread thread = new Thread(this);
		thread.start();
	}

	/**
	 * アニメーションを停止する．
	 */
	public void stop() {
		running = false;
	}

	/**
	 * アニメーションで動かした図形を元の位置に戻す．
	 */
	public void reset() {
		model.setAnimationMode(false);
		for(int i=0; i<orbits.size(); i++) {
			orbits.get(i).reset();
		}
		model.updated();
		updated();
	}

	@Override
	/**
	 * アニメーションが実行されるスレッド．必ず executre から呼び出されるようにする．
	 */
	public void run() {
		long startTime = System.nanoTime();
		long nowTime;
		boolean executing;
		while(running) {
			executing = false;
			nowTime = System.nanoTime();
			for(int i=0; i<orbits.size(); i++) {
				 executing = orbits.get(i).move(nowTime - startTime) | executing;
			}
			model.updated();
			if(!executing) running = false;
		}
		reset();

	}

	/**
	 * アニメーションに更新があったときに呼び出す．
	 */
	public void updated() {
		setChanged();
		notifyObservers();
	}

}

/**
 * 軌跡を表す関数行列 A(t) と Figure の組を保持する．
 */
class Orbit implements Serializable {

	/**
	 * 動かす Figure の集合
	 */
	private ArrayList<Figure> fig;

	/**
	 * 行列関数 A(t) の式
	 * [1] [3] [5]
	 * [2] [4] [6]
	 */
	private ArrayList<String> function;

	/**
	 * 開始時間
	 */
	private long startTime;

	/**
	 * 動作時間
	 */
	private long operatingTime;

	/**
	 * 名前
	 */
	private String name;

	/**
	 * 何もしないアニメーションで初期化した新しい Orbit を作成する．
	 */
	Orbit(){
		fig = new ArrayList<Figure>();
		function = new ArrayList<String>(6);
		function.add("1");
		function.add("0");
		function.add("0");
		function.add("1");
		function.add("0");
		function.add("0");
		startTime = 0;
		operatingTime = 0;
		name = "恒等変換";
	}

	/**
	 * 引数から新しい Orbit を作成する．
	 * @param f Figure の集合
	 * @param func 軌跡を表す関数行列 A(t)
	 * @param startT 開始時間
	 * @param operateT 終了時間
	 * @param nam 名前
	 */
	public Orbit(ArrayList<Figure> f, ArrayList<String> func, long startT, long operateT, String nam){
		fig = new ArrayList<Figure>(f);
		function = new ArrayList<String>(func);
		startTime = startT;
		operatingTime = operateT;
		name = nam;
	}

	/**
	 * 図形を動かす．
	 * @param t ナノ時間
	 * @return 実行時間を過ぎてないかどうか
	 */
	public boolean move(long t) {
		if(t < startTime) return true;
		if(startTime + operatingTime < t) return false;

		AffineTransform afn = culcAffine(t - startTime);
		for(int i=0; i<fig.size(); i++) {
			fig.get(i).setDrawingAffine(afn);
		}
		return true;
	}

	/**
	 * 動かした図形を元の位置に戻す．
	 */
	public void reset() {
		AffineTransform afn = new AffineTransform(); // 恒等変換
		for(int i=0; i<fig.size(); i++) {
			fig.get(i).setDrawingAffine(afn);
		}
	}

	/**
	 * 関数行列 A(t) が、時間 t のときの行列を計算する．
	 * @param t ナノ時間
	 * @return 変換行列
	 */
	private AffineTransform culcAffine(long t) {
		double d[] = new double[6];
		for(int i=0; i<6; i++) {
			d[i] = culcFormula(function.get(i), t);
		}

		return new AffineTransform(d);
	}

	/**
	 * 文字列で与えられた式を計算する．
	 * @param fmr 式
	 * @param t ナノ時間
	 * @return 計算された値
	 */
	private static double culcFormula(String fmr, long t) {
		return new Parser(fmr, t/1E9).expr();
	}

	public long getStartTime() {
		return startTime;
	}

	public long getOperatingTime() {
		return operatingTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFunction(int i) {
		return function.get(i);
	}

	public ArrayList<Figure> getFigures(){
		return fig;
	}
}
