package com.example.test2048;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Point;

import com.example.test2048.GameView.GameTable;
import com.example.test2048.GameView.MessageHandler;
import static com.example.test2048.GameView.ENTITY_COLORS;
class ViewEntity {

	int number;
	@Deprecated
	int color;
	int textColor;
	Point oldPoint;
	Point point;
	ViewEntity fromEntity[] = new ViewEntity[2];
	boolean newFlag;
	
	public static final ViewEntity EMPTY_ENTITY = new ViewEntity();

	private static final String SPLITE_CHAR = ",";

	private ViewEntity() {
		point = new Point(-1, -1);
		oldPoint = new Point(-1, -1);
	}

	public static ViewEntity createNewEntity(String serialize) {
		ViewEntity entity = new ViewEntity();
		String splites[] = serialize.split(SPLITE_CHAR);
		entity.number = Integer.parseInt(splites[0]);
		entity.point.x = Integer.parseInt(splites[1]);
		entity.point.y = Integer.parseInt(splites[2]);
		entity.oldPoint.x = Integer.parseInt(splites[3]);
		entity.oldPoint.y = Integer.parseInt(splites[4]);
		entity.newFlag = Boolean.parseBoolean(splites[5]);
		if ((entity.point.x == EMPTY_ENTITY.point.x)
				&& (entity.point.y == EMPTY_ENTITY.point.y)) {
			return EMPTY_ENTITY;
		}
		return entity;
	}

	public static ViewEntity createNewEntityFromMap(GameTable table,
			Point point, int number) {
		ViewEntity e = createNewEntity(point, number);
		table.addEntity(e);
		return e;
	}

	public static ViewEntity createNewEntity(Point point, int number) {
		ViewEntity entity = new ViewEntity();
		entity.oldPoint.x = entity.point.x;
		entity.oldPoint.y = entity.point.y;
		entity.point.x = point.x;
		entity.point.y = point.y;
		entity.number = number;
		entity.newFlag = true;
//		entity.color = getColor(entity.number);
		return entity;
	}

	public static ViewEntity testCreateNewEntity(GameTable table,int number) {
		ViewEntity entity = new ViewEntity();
		Random r = new Random();
		entity.number = number;
//		entity.color = getColor(entity.number);
		entity.point.x = r.nextInt(4);
		entity.point.y = r.nextInt(4);
		entity.newFlag = true;
		// table.updateTableView(entity.point);
		
		entity = createNewEntityFromMap(table);
		entity.number = number;
		return entity;
	}

	public static ViewEntity createNewEntityFromMap(GameTable table,
			MessageHandler handler) {
		ViewEntity entity = new ViewEntity();
		ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < GameTable.LENGTH; i++) {
			for (int j = 0; j < GameTable.LENGTH; j++) {
				Point p = new Point(i, j);
				if (table.getEntity(p) == EMPTY_ENTITY) {
					points.add(new Point(i, j));
				}
			}
		}
		if (points.size() == 0) {
			return null;
		}
		Random r = new Random();
		int seed = r.nextInt(10) % 9 == 0 ? 4 : 2;
		entity.number = seed;
//		entity.color = getColor(entity.number);
		entity.point = points.get(r.nextInt(points.size()));
		entity.oldPoint = new Point(EMPTY_ENTITY.point);
		entity.newFlag = true;
		table.addEntity(entity);
		if (handler != null) {
			handler.postNewEntityCreate();
		}
		return entity;
	}

	public static ViewEntity createNewEntityFromMap(GameTable table) {
		return createNewEntityFromMap(table, null);
	}

	public boolean isNew() {
		return newFlag;
	}

	public boolean isTranslate() {
		return oldPoint != null && oldPoint.x != -1 && oldPoint.y != -1
				&& !oldPoint.equals(point);
	}

	public int getColor() {
		int pos =  log(number,2);
		return ENTITY_COLORS[pos-1];
	}
	
	public int getTextColor() {
		if (number <= 16 || number > 8192) {
			return 0xff000000;
		}
		return 0xffffffff;
	}
	
	static int log(double v1, double v2){
		return (int)(Math.log(v1)/Math.log(v2));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewEntity other = (ViewEntity) obj;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ViewEntity [number=" + number + ", oldPoint=" + oldPoint
				+ ", point=" + point + ", newFlag=" + newFlag + "]";
	}

	public String serialize() {
		StringBuilder sbuilder = new StringBuilder();
		sbuilder.append(number).append(SPLITE_CHAR).append(point.x)
				.append(SPLITE_CHAR).append(point.y).append(SPLITE_CHAR)
				.append(oldPoint.x).append(SPLITE_CHAR).append(oldPoint.y)
				.append(SPLITE_CHAR).append(newFlag);
		return sbuilder.toString();
	}
}
