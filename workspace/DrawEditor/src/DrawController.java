
import javax.swing.*;
import java.awt.event.*;

/**
 * ViewPanel のアクションを受け取り， DrawModel を操作する．また，グリッドに関する調整も行う．
 */
class DrawController implements MouseListener, MouseMotionListener, KeyListener{
	protected DrawFrame frame;
	protected Pen pen;
	protected DrawModel model;
	protected MyPopupMenu popup;

	/**
	 * ドラッグし始めた座標
	 */
	protected int dragStartX, dragStartY;
	/**
	 * カーソルの座標
	 */
	protected int cursorX, cursorY;

	/**
	 * スペースキーが押されている状態かどうか
	 */
	protected boolean pushdown;
	/**
	 * コントロールキーが押されている状態かどうか
	 */
	protected boolean controlDown;
	/**
	 * シフトキーが押されている状態かどうか
	 */
	protected boolean shiftDown;

	/**
	 * スポイト機能を使う際に true を返す．
	 */
	protected boolean getColorMode;

	/**
	 * 新しい DrawController を作成する．
	 * @param frm DrawFrame
	 * @param p Pen
	 * @param a DrawModel
	 * @param pop MyPopupMenu
	 */
	public DrawController(DrawFrame frm, Pen p, DrawModel a, MyPopupMenu pop) {
		frame = frm;
		pen = p;
		model = a;
		popup = pop;

		pushdown = false;
		controlDown = false;
		getColorMode = false;
	}

	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * ViewPanel がクリックされた時に呼び出される．
	 */
	public void mousePressed(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e)) {
			// 右クリックされたとき，ポップアップメニューを表示します．
			popup.show(e.getComponent(), e.getX(), e.getY());
			return;
		}
		pressed(e.getX(), e.getY());
	}

	/**
	 * ViewPanel でドラッグされた時に呼び出される．
	 */
	public void mouseDragged(MouseEvent e) {
		dragged(e.getX(), e.getY());
	}

	/**
	 * ViewPanel でマウスが離された時に呼び出される．
	 */
	public void mouseReleased(MouseEvent e) {
		released(e.getX(), e.getY());
		if(model.getSelectMode()) {

		}else {
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * ViewPanel でマウスが動かされた時に呼び出される．スペースキーがマウスのボタンと同じ動作をする．
	 */
	public void mouseMoved(MouseEvent e) {
		if(pushdown) dragged(e.getX(), e.getY());
		else moved(e.getX(), e.getY());
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	/**
	 * キーが押された時に呼び出される．カーソルを動かしたり，ショートカットキーの機能を動作させたりする．
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {

		case KeyEvent.VK_SPACE:
			if(!pushdown)pressed(cursorX, cursorY);
			pushdown = true;
			break;

		case KeyEvent.VK_LEFT:
			cursorX -= model.getStep();
			break;

		case KeyEvent.VK_RIGHT:
			cursorX += model.getStep();
			break;
		case KeyEvent.VK_UP:
			cursorY -= model.getStep();
			break;

		case KeyEvent.VK_DOWN:
			cursorY += model.getStep();
			break;

		case KeyEvent.VK_CONTROL:
			controlDown = true;
			break;

		case KeyEvent.VK_SHIFT:
			shiftDown = true;
			break;

		case KeyEvent.VK_S:
			if(controlDown) model.save();
			break;

		case KeyEvent.VK_O:
			if(controlDown) model.load();
			break;

		case KeyEvent.VK_Z:
			if(controlDown) {
				if(shiftDown) model.redo();
				else model.undo();
			}
			break;

		case KeyEvent.VK_X:
			if(controlDown) model.cut();
			break;

		case KeyEvent.VK_C:
			if(controlDown) model.copy();
			break;

		case KeyEvent.VK_V:
			if(controlDown) model.paste();
			break;

		case KeyEvent.VK_D:
			if(controlDown) model.deleteSelectedFigure();
			break;

		case KeyEvent.VK_E:
			if(controlDown) model.setSelectMode(!model.getSelectMode());
			break;

		case KeyEvent.VK_G:
			if(controlDown) {
				if(shiftDown) model.ungrouping();
				else model.grouping();
			}
			break;

		default:
			return;
		}
		if(pushdown) dragged(cursorX, cursorY);
		model.updated();
	}

	@Override
	/**
	 * キーが離された時に呼び出される．
	 */
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {

		case KeyEvent.VK_SPACE:
			pushdown = false;
			released(cursorX, cursorY);
			break;

		case KeyEvent.VK_CONTROL:
			controlDown = false;
			break;

		case KeyEvent.VK_SHIFT:
			shiftDown = false;
			break;
		}
	}

	/**
	 * マウスやスペースキーが押された時に呼び出される．
	 * @param x クリックされた x 座標
	 * @param y クリックされた y 座標
	 */
	private void pressed(int x, int y) {
		if(getColorMode) {
			pen.setColor(frame.getColor(x, y));
			getColorMode = false;
			return;
		}
		dragStartX = x - (x%model.getStep()) + (model.getStep()/2);
		dragStartY = y - (y%model.getStep()) + (model.getStep()/2);
		if(model.getSelectMode()) {
			model.selectEditFigure(x, y);
		}else {
			model.createFigure(dragStartX, dragStartY);
		}
	}

	/**
	 * ドラッグされた時に呼び出される．
	 * @param x マウスの x 座標
	 * @param y マウスの y 座標
	 */
	private void dragged(int x, int y) {
		cursorX = x - (x%model.getStep()) + (model.getStep()/2);
		cursorY = y - (y%model.getStep()) + (model.getStep()/2);
		if(model.getSelectMode()) {
			model.editFigure(dragStartX, dragStartY, cursorX, cursorY);
		}else {
			model.reshapeFigure(dragStartX, dragStartY, cursorX, cursorY);
		}
		model.updated();
	}

	/**
	 * マウスやスペースキーが離された時に呼び出される．
	 * @param x マウスの x 座標
	 * @param y マウスの y 座標
	 */
	private void released(int x, int y) {
		if(model.getSelectMode()) {
			model.resetEditing(dragStartX, dragStartY, cursorX, cursorY, controlDown);
		}else {
			model.completeFigure(dragStartX, dragStartY, cursorX, cursorY);
		}
	}

	/**
	 * マウスが動かされた時に呼び出される．
	 * @param x マウスの x 座標
	 * @param y マウスの y 座標
	 */
	private void moved(int x, int y) {
		if(model.getSelectMode()) {
			cursorX = x;
			cursorY = y;
		}else {
			cursorX = x - (x%model.getStep()) + (model.getStep()/2);
			cursorY = y - (y%model.getStep()) + (model.getStep()/2);
		}
		model.updated();
	}

	public int getCursorX() {
		return cursorX;
	}

	public int getCursorY() {
		return cursorY;
	}

	public void setGetColorMode(boolean gcm) {
		getColorMode = gcm;
	}
}
