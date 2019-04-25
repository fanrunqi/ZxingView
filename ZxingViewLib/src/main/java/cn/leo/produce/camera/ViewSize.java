package cn.leo.produce.camera;

/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/19 10:40
 */
public class ViewSize {

    public int width;
    public int height;

    public ViewSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ViewSize Rotate() {
        return new ViewSize(height, width);
    }
}
