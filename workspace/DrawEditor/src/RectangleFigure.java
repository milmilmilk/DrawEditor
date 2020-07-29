
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class RectangleFigure extends Figure {
	public RectangleFigure(int x, int y, int w, int h, Color c) {
		super(x, y, w, h, c);
		// 引数付きのコンストラクタは継承されないので，コンストラクタを定義．
		// superで親のコンストラクタを呼び出すだけ．
	}

	public void draw(Graphics g) {
		g.setColor(color);
		g.drawRect(x, y, width, height);
	}
}

class OvalFigure extends Figure {
	public OvalFigure(int x, int y, int w, int h, Color c) {
		super(x, y, w, h, c);
		// 引数付きのコンストラクタは継承されないので，コンストラクタを定義．
		// superで親のコンストラクタを呼び出すだけ．
	}

	public void draw(Graphics g) {
		g.setColor(color);
		g.drawOval(x, y, width, height);
	}
}