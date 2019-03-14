package id.co.coffeecode.learntextrecognitionapps.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class TextGraphic extends GraphicOverlay.Graphic {

    private static final int TEXT_COLOR = Color.BLUE;
    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;

    private final Paint reactPaint, textPaint;
    private final FirebaseVisionText.Element text;

    public TextGraphic(GraphicOverlay overlay, FirebaseVisionText.Element text) {
        super(overlay);

        this.text = text;

        reactPaint  = new Paint();
        reactPaint.setColor(TEXT_COLOR);
        reactPaint.setStyle(Paint.Style.STROKE);
        reactPaint.setStrokeWidth(STROKE_WIDTH);

        textPaint  = new Paint();
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(TEXT_SIZE);

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        if (text == null){
            throw new IllegalStateException("Attempting to draw a null text");
        }

        RectF rect =  new RectF(text.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, reactPaint);

        canvas.drawText(text.getText(), rect.left, rect.bottom, textPaint);
    }
}
