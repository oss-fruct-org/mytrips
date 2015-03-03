package org.fruct.oss.audioguide.overlays;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.MotionEvent;

import org.fruct.oss.audioguide.App;
import org.fruct.oss.audioguide.R;
import org.fruct.oss.audioguide.files.DefaultFileManager;
import org.fruct.oss.audioguide.files.FileManager;
import org.fruct.oss.audioguide.track.CursorHolder;
import org.fruct.oss.audioguide.track.CursorReceiver;
import org.fruct.oss.audioguide.track.Point;
import org.fruct.oss.audioguide.util.AUtils;
import org.fruct.oss.audioguide.util.Utils;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EditOverlay extends Overlay implements Closeable {
	private final Context context;
	private final MapView mapView;

	public interface Listener {
		void pointMoved(Point p, IGeoPoint geoPoint);
		void pointPressed(Point p);
		void pointLongPressed(Point p);
	}

	private final static Logger log = LoggerFactory.getLogger(EditOverlay.class);
	private final static int[] markers = {R.drawable.marker_1,
			R.drawable.marker_2,
			R.drawable.marker_3};

    private static Drawable defaultMarker;
    private static Drawable startMarker;
    private static int markerHeight, markerWidth;

    private static Map<Long, Integer> trackColors = new HashMap<Long, Integer>();
	private Map<Long, EditOverlayItem> items = new HashMap<Long, EditOverlayItem>();
	private int itemSize;

	private final Paint itemBackgroundDragPaint;
	private final Paint itemBackgroundPaint;

	private final FileManager fileManager;

	private Rect markerPadding;
	private Drawable markerDrawable;
	private Drawable markerDrawable2;

	private Paint linePaint;

	private EditOverlayItem draggingItem;
	private int dragRelX;
	private int dragRelY;
	private int dragStartX;
	private int dragStartY;
	private boolean dragStarted;

	private boolean isEditable = false;

	private transient android.graphics.Point point = new android.graphics.Point();
	private transient android.graphics.Point point2 = new android.graphics.Point();
	private transient android.graphics.Point point3 = new android.graphics.Point();

	private transient HitResult hitResult = new HitResult();

	private Listener listener;

	private final CursorHolder relationsCursorHolder;
	private Cursor currentRelationsCursor;

	private final CursorHolder pointsCursorHolder;
	private Cursor currentPointsCursor;

	private final List<Pair<Long, Long>> relations = new ArrayList<Pair<Long, Long>>();
    private final List<TrackRelation> tracks = new ArrayList<TrackRelation>();

	public EditOverlay(Context ctx, CursorHolder pointsCursorHolder,
					   CursorHolder relationsCursorHolder, int markerIndex, MapView mapView) {
		super(ctx);

		this.mapView = mapView;
		this.context = ctx;
		this.pointsCursorHolder = pointsCursorHolder;
		this.relationsCursorHolder = relationsCursorHolder;

		pointsCursorHolder.attachToReceiver(pointsCursorReceiver);
		if (relationsCursorHolder != null)
			relationsCursorHolder.attachToReceiver(relationsCursorReceiver);

		itemSize = Utils.getDP(24);
        markerHeight = Utils.getDP((int)(59 / 2.5f));
        markerWidth = Utils.getDP((int)(40 / 2.5f));
		itemBackgroundDragPaint = new Paint();
		itemBackgroundDragPaint.setColor(0xff1143fa);
		itemBackgroundDragPaint.setStyle(Paint.Style.FILL);

		linePaint = new Paint();
		linePaint.setColor(0xff1143fa);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(4);
		linePaint.setAntiAlias(true);

		itemBackgroundPaint = new Paint();
		itemBackgroundPaint.setColor(0xffffffff);
		itemBackgroundPaint.setStyle(Paint.Style.FILL);
		itemBackgroundPaint.setTextSize(itemSize);
		itemBackgroundPaint.setAntiAlias(true);
		itemBackgroundPaint.setTextAlign(Paint.Align.CENTER);


        defaultMarker = App.getContext().getResources().getDrawable(R.drawable.point_marker);
        startMarker = App.getContext().getResources().getDrawable(R.drawable.start_point_marker);
        fileManager = DefaultFileManager.getInstance();
	}

	@Override
	public void close() {
		pointsCursorHolder.close();
		if (relationsCursorHolder != null)
			relationsCursorHolder.close();

		fileManager.recycleAllBitmaps("edit-overlay");
	}

	private int getMeanColor(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

		drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
		Canvas canvas = new Canvas(bitmap);
		drawable.draw(canvas);

		int r = 0, g = 0, b = 0;
		Random rand = new Random();

		int c = 0;
		for (int i = 0; i < 20; i++) {
			int x = rand.nextInt(bitmap.getWidth());
			int y = rand.nextInt(bitmap.getHeight());

			int pix = bitmap.getPixel(x, y);

			int a = (pix >>> 24) & 0xff;

			if (a > 200) {
				c++;
				r += (pix >>> 16) & 0xff;
				g += (pix >>> 8) & 0xff;
				b += (pix) & 0xff;
			}
		}

		if (c > 0) {
			r /= c;
			g /= c;
			b /= c;
		}

		bitmap.recycle();
		return (r << 16) + (g << 8) + b + 0xff000000;
	}

    public boolean checkIsStartPoint(Long id){
        if(tracks.size() == 0)
            return false;
        for(TrackRelation tr : tracks){
            if(tr.getStartPoint() == id)
                return true;
        }
        return false;
    }
    public int getTrackColor(long trackId){
        if(trackColors.get(trackId) != null)
            return trackColors.get(trackId);
        int r = 0, g = 0, b = 0;
        Random rand = new Random();

        r = rand.nextInt(128);
        g = rand.nextInt(128);
        b = rand.nextInt(128);

        int result =  (r << 16) + (g << 8) + b + 0xff000000;
        trackColors.put(trackId, result);
        return result;
    }


	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	protected void draw(Canvas canvas, MapView view, boolean shadow) {
		if (shadow)
			return;

		drawPath(canvas, view);
		drawItems(canvas, view);
	}

	private void drawPath(Canvas canvas, MapView view) {
		/*for (Pair<Long, Long> line : relations) {
			EditOverlayItem p1 = items.get(line.first);
			EditOverlayItem p2 = items.get(line.second);

			if (p1 != null && p2 != null)
				drawLine(canvas, view, p1, p2);
		}*/

        for(TrackRelation tr : tracks){
            linePaint.setColor(tr.getColor());
            while(tr.hasNext()){
                Pair<Long, Long> p = tr.getPair();
                EditOverlayItem p1 = items.get(p.first);
                EditOverlayItem p2 = items.get(p.second);

                if (p1 != null && p2 != null)
                    drawLine(canvas, view, p1, p2);
            }
        }
	}

	// TODO: projection points can be performed only if map position changes
	private void drawLine(Canvas canvas, MapView view, EditOverlayItem item, EditOverlayItem item2) {
		Projection proj = view.getProjection();
		proj.toPixels(item.geoPoint, point);
		proj.toPixels(item2.geoPoint, point2);

		canvas.drawLine(point.x, point.y, point2.x, point2.y, linePaint);
	}

	private void drawItems(Canvas canvas, MapView view) {
        for(Map.Entry<Long, EditOverlayItem> entry : items.entrySet())
            drawItem(canvas,view,entry.getValue(),checkIsStartPoint(entry.getKey()));
	}

	private void drawItem(Canvas canvas, MapView view, EditOverlayItem item, boolean isStart) {
		Projection proj = view.getProjection();
		proj.toPixels(item.geoPoint, point);

        if(!isStart) {
            defaultMarker.setBounds(point.x - markerWidth, point.y - markerHeight * 2, point.x + markerWidth, point.y);
            defaultMarker.draw(canvas);
        }else{
            startMarker.setBounds(point.x - markerWidth,point.y - markerHeight * 2, point.x + markerWidth, point.y);
            startMarker.draw(canvas);
        }
	}



	public boolean testHit(MotionEvent e, MapView mapView, EditOverlayItem item, HitResult result) {
		final Projection proj = mapView.getProjection();
		final Rect screenRect = proj.getIntrinsicScreenRect();

		final int x = screenRect.left + (int) e.getX();
		final int y = screenRect.top + (int) e.getY();

		proj.toPixels(item.geoPoint, point);

		final int ix = point.x - x;
		final int iy = point.y - y;

		if (result != null) {
			result.item = item;
			result.relHookX = ix;
			result.relHookY = iy;
		}

		return ix >= -itemSize && iy >= 0 && ix <= itemSize && iy <= 2 * itemSize;
	}

	public HitResult testHit(MotionEvent e, MapView mapView) {
		for (EditOverlayItem item : items.values()) {
			if (testHit(e, mapView, item, hitResult))
				return hitResult;
		}

		return null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			HitResult hitResult = testHit(event, mapView);

			if (hitResult != null) {
				draggingItem = hitResult.item;
				dragRelX = hitResult.relHookX;
				dragRelY = hitResult.relHookY;
				dragStartX = (int) event.getX();
				dragStartY = (int) event.getY();
				dragStarted = false;

				//if (draggingItem.data.isEditable())
					setupLongPressHandler(draggingItem);

				mapView.invalidate();
				return false;
			} else {
				return false;
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP && draggingItem != null) {
			if (dragStarted) {
				if (listener != null) {
					listener.pointMoved(draggingItem.data, draggingItem.geoPoint);
				}
			} else {
				if (listener != null) {
					listener.pointPressed(draggingItem.data);
				}
			}

			draggingItem = null;
			mapView.invalidate();
			return false;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE
				&& draggingItem != null
				&& draggingItem.data.isEditable()) {
			final int dx = dragStartX - (int) event.getX();
			final int dy = dragStartY - (int) event.getY();

			if (dragStarted || dx * dx + dy * dy > 32 * 32) {
				dragStarted = true;
				moveItem(draggingItem, event, mapView);
			}
			return true;
		} else {
			return false;
		}
	}

	private void setupLongPressHandler(final EditOverlayItem requestedItem) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (draggingItem == requestedItem && !dragStarted) {
					if (listener != null) {
						listener.pointLongPressed(draggingItem.data);
					}
					draggingItem = null;
				}
			}
		}, 500);
	}

/*
private void checkDistance(MapView mapView, android.graphics.Point p) {
		MapView.Projection proj = mapView.getProjection();

		double min = Long.MAX_VALUE;
		for (Pair<Long, Long> line : relations) {
			EditOverlayItem p1 = items.get(line.first);
			EditOverlayItem p2 = items.get(line.second);

			if (p1 != null && p2 != null) {
				proj.toPixels(p1.geoPoint, point);
				proj.toPixels(p2.geoPoint, point2);

				min = Math.min(min, AUtils.distanceToLine(point, point2, p));
			}
		}

		log.debug("Min distance: " + min);
	}
*/

	private void moveItem(EditOverlayItem item, MotionEvent e, MapView mapView) {
		final Projection proj = mapView.getProjection();

		point3.set((int) e.getX() + dragRelX, (int) e.getY() + dragRelY);
		IGeoPoint ret = proj.fromPixels(point3.x, point3.y);
		item.geoPoint = AUtils.copyGeoPoint(ret);

		mapView.invalidate();
	}

	private CursorReceiver pointsCursorReceiver = new CursorReceiver() {
		@Override
		public Cursor swapCursor(Cursor cursor) {
			Cursor oldCursor = currentPointsCursor;
			currentPointsCursor = cursor;

			items.clear();
			Point.CursorFields cf = Point.getCursorFields(cursor);

			while (cursor.moveToNext()) {
				Point point = new Point(cursor);
				long id = cursor.getLong(cf._id);

				EditOverlayItem item = new EditOverlayItem(
						new GeoPoint(point.getLatE6(), point.getLonE6()), point);

				if (item.data.hasPhoto()) {
					fileManager.requestImageBitmap(item.data.getPhotoUrl(),
							Utils.getDP(48), Utils.getDP(48), FileManager.ScaleMode.SCALE_CROP, new EditOverlayBitmapSetter(item), "edit-overlay");
				}

				items.put(id, item);
			}
			mapView.invalidate();

			return oldCursor;
		}
	};

    /*
	private CursorReceiver relationsCursorReceiver = new CursorReceiver() {
		@Override
		public Cursor swapCursor(Cursor cursor) {
			Cursor oldCursor = currentPointsCursor;
			currentPointsCursor = cursor;

			long currentTrackId = -1;
			long prevPointId = -1;

			while (cursor.moveToNext()) {
				long trackId = cursor.getLong(0);

				if (trackId != currentTrackId) {
					currentTrackId = trackId;
					prevPointId = cursor.getLong(1);
					continue;
				}

				long currentPointId = cursor.getLong(1);

				relations.add(Pair.create(prevPointId, currentPointId));
				prevPointId = currentPointId;
			}

			return oldCursor;
		}
	}; */

    private CursorReceiver relationsCursorReceiver = new CursorReceiver() {
        @Override
        public Cursor swapCursor(Cursor cursor) {
            Cursor oldCursor = currentPointsCursor;
            currentPointsCursor = cursor;
            tracks.clear();
            long currentTrackId = -1;
            long prevPointId = -1;
            List<Long>points = new ArrayList<Long>();
            while (cursor.moveToNext()) {
                long trackId = cursor.getLong(0);
                TrackRelation tr = new TrackRelation(-1L);
                if (trackId != currentTrackId) { // new track
                    if(points.size() != 0 && currentTrackId != -1){
                        tr = new TrackRelation(currentTrackId);
                        for(Long p : points)
                            tr.addPoint(p);
                        tracks.add(tr);
                        points.clear();
                    }

                    currentTrackId = trackId;
                    prevPointId = cursor.getLong(1);
                    points.add(prevPointId);
                    continue;
                }

                long currentPointId = cursor.getLong(1);
                points.add(currentPointId);
                relations.add(Pair.create(prevPointId, currentPointId));
                prevPointId = currentPointId;
            }
            if(points.size() != 0 && currentTrackId != -1){
                TrackRelation tr = new TrackRelation(currentTrackId);
                for(Long p : points)
                    tr.addPoint(p);
                tracks.add(tr);
                points.clear();
            }
            return oldCursor;
        }
    };


    class EditOverlayItem {
		EditOverlayItem(GeoPoint geoPoint, Point data) {
			this.geoPoint = geoPoint;
			this.data = data;
		}
		Point data;
		GeoPoint geoPoint;
		Bitmap iconBitmap;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			EditOverlayItem that = (EditOverlayItem) o;

			if (!data.equals(that.data)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return data.hashCode();
		}
	}

	class HitResult {
		EditOverlayItem item;
		int relHookX;
		int relHookY;
	}

	class EditOverlayBitmapSetter implements FileManager.BitmapSetter {
		private final EditOverlayItem item;
		private Bitmap bitmap;
		private Handler handler = new Handler(Looper.getMainLooper());

		public EditOverlayBitmapSetter(EditOverlayItem item) {
			this.item = item;
		}

		private Object tag;

		@Override
		public void bitmapReady(final Bitmap newBitmap) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					item.iconBitmap = newBitmap;
					recycle();
					bitmap = newBitmap;
					mapView.invalidate();
				}
			});
		}

		@Override
		public void recycle() {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
		}

		@Override
		public void setTag(Object tag) {
			this.tag = tag;
		}

		@Override
		public Object getTag() {
			return tag;
		}
	}

    class TrackRelation{

        Long trackId = -1l;
        List<Long> points;
        int index = 0;
        int color;
        public TrackRelation(Long trackId){
            this.trackId = trackId;
            points = new ArrayList<Long>();
            color = getTrackColor(trackId);
        }

        public void addPoint(Long pointId){
            points.add(pointId);
        }

        public boolean hasNext(){
            if( index < points.size()-1) {
                return true;
            }else{
                index = 0;
                return false;
            }
        }

        public Pair<Long, Long> getPair(){
            Pair<Long, Long> p =  new Pair<Long, Long>(points.get(index), points.get(index + 1));
            index++;
            return p;
        }

        public Long getStartPoint(){
            if(points.size() > 0)
                return points.get(0);
            else
                return -1L;
        }

        public int  getColor(){
            return color;
        }
    }

}
