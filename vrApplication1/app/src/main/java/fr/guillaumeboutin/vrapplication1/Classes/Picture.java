package fr.guillaumeboutin.vrapplication1.Classes;

import io.realm.RealmObject;

/**
 * Created by guillaumeboutin on 27/03/2017.
 */

public class Picture extends RealmObject {
    private int    id;
    private String name;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //Drawable d = Drawable.createFromStream(new ByteArrayInputStream(ARRAY_BYTES), null);

    /*
    byte[] blob=c.getBlob("yourcolumnname");
    Bitmap bmp=BitmapFactory.decodeByteArray(blob,0,blob.length);
    ImageView image=new ImageView(this);
    image.setImageBitmap(bmp);

    public byte[] getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, bos);
        return bos.toByteArray();
    }
    public Bitmap getBitmap(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap , 0, bitmap.length);
    }

    BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        textView.setText("Radius: " + "" + radiusArr[position]);
        Bitmap blurred = blurRenderScript(bitmap, radiusArr[position]);//second parametre is radius
        imageview.setImageBitmap(blurred);

        private Bitmap blurRenderScript(Bitmap smallBitmap, int radius) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }
    */
}
