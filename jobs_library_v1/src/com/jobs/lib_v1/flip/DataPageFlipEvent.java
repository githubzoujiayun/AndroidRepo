package com.jobs.lib_v1.flip;

import com.jobs.lib_v1.app.AppUtil;

import android.view.MotionEvent;

/**
 * 截获界面用户触控事件的抽象类
 *
 * @author solomon.wen
 * @date 2012-09-15
 */
public class DataPageFlipEvent {
	public final static int minDistance = 10;
	public String TAG = "";
	public boolean debug = false;
	private float _first_pos_x = 0;
	private float _first_pos_y = 0;
	private float _last_delt_x = 0;
	private float _last_delt_y = 0;
	private float _last_pos_x = 0;
	private float _last_pos_y = 0;
	private boolean _event_started = false;
	private boolean _has_move_events = false;
	private boolean _can_move = false;
	private int _last_action = -1;
	private int _last_move_pos = 0;
	private boolean isEventX = true;
	private boolean _prev_can_move = false;
	private boolean _prev_move_left = false;
	private boolean _prev_move_right = false;
	private boolean _need_check_xy_pos = false;

	public DataPageFlipEvent(boolean eventX){
		isEventX = eventX;
	}

	public boolean getPrevCanMove(){
		return _prev_can_move && ((isEventX && getMovedDistanceX() >= minDistance) || !isEventX && getMovedDistanceY() >= minDistance);
	}

	public boolean isOnTouchEvent(){
		return _event_started;
	}

	public boolean hasMoveEvents(){
		return _has_move_events;
	}

	public boolean canMove(){
		return _can_move && ((isEventX && getMovedDistanceX() >= minDistance) || !isEventX && getMovedDistanceY() >= minDistance);
	}

	public int getMovePos(){
		return _last_move_pos;
	}

	public float getMovedDistanceX(){
		return Math.abs(_last_pos_x - _first_pos_x);
	}

	public float getMovedDistanceY(){
		return Math.abs(_last_pos_y - _first_pos_y);
	}

	public float getDeltX(){
		return _last_delt_x;
	}

	public float getDeltY(){
		return _last_delt_y;
	}

	public float getLastX(){
		return _last_pos_x;
	}

	public float getLastY(){
		return _last_pos_y;
	}
	
	public boolean isTouchedDown(){
		return (_last_action == MotionEvent.ACTION_DOWN);
	}
	
	public boolean isTouchedMove(){
		return (_last_action == MotionEvent.ACTION_MOVE);
	}
	
	public boolean prevMovedRight(){
		return _prev_move_right;
	}
	
	public boolean prevMovedLeft(){
		return _prev_move_left;
	}
	
	public boolean lastMovedRight(){
		if(!_has_move_events){
			return false;
		}
		
		return _last_move_pos > 0;
	}
	
	public boolean lastMovedLeft(){
		if(!_has_move_events){
			return false;
		}
		
		return _last_move_pos < 0;
	}

	public boolean isTouchedUp(){
		return (_last_action == MotionEvent.ACTION_UP);
	}
	
	public boolean isTouchedCancel(){
		return (_last_action == MotionEvent.ACTION_CANCEL);
	}

	public void recvTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		int _old_action = _last_action;

		_last_action = event.getAction();
		
		_last_delt_x = _last_pos_x - x;
		_last_delt_y = _last_pos_y - y;
		
		if(isEventX){
			_last_move_pos = (int)_last_delt_x;
		} else {
			_last_move_pos = (int)_last_delt_y;
		}

		if(debug){
			switch (_last_action) {
				case MotionEvent.ACTION_DOWN:
					AppUtil.print(TAG + "::MotionEvent.ACTION_DOWN");
		
					break;
		
				case MotionEvent.ACTION_MOVE:
					AppUtil.print(TAG + "::MotionEvent.ACTION_MOVE");
		
					break;
		
				case MotionEvent.ACTION_UP:
					AppUtil.print(TAG + "::MotionEvent.ACTION_UP");
					break;
		
				case MotionEvent.ACTION_CANCEL:
					AppUtil.print(TAG + "::MotionEvent.ACTION_CANCEL");
					break;
			}
		}

		switch (_last_action) {
			case MotionEvent.ACTION_DOWN:
				_can_move = false;
				_prev_can_move = false;
				_event_started = true;
				_need_check_xy_pos = true;
				_prev_move_left = false;
				_prev_move_right = false;
				_first_pos_x = x;
				_first_pos_y = y;
				break;
	
			case MotionEvent.ACTION_MOVE:
				if (_old_action != _last_action || _need_check_xy_pos) {
					if(_last_delt_x != _last_delt_y){
						_need_check_xy_pos = false;
						
						if(isEventX){
							_can_move = Math.abs(_last_delt_x) > Math.abs(_last_delt_y);
						} else {
							_can_move = Math.abs(_last_delt_y) > Math.abs(_last_delt_x);
						}
					}
				}
				
				_prev_move_left = _last_move_pos < 0;
				_prev_move_right = _last_move_pos > 0;

				_prev_can_move = _can_move;
	
				if (_can_move) {
					_has_move_events = true;
				}
	
				break;
	
			case MotionEvent.ACTION_UP:
				_need_check_xy_pos = false;
				_can_move = false;
				_event_started = false;
				break;
	
			case MotionEvent.ACTION_CANCEL:
				_need_check_xy_pos = false;
				_can_move = false;
				_event_started = false;
				break;
		}
		
		_last_pos_y = y;
		_last_pos_x = x;
	}
}
