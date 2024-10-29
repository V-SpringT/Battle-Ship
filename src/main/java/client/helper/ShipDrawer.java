/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.helper;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class ShipDrawer {

    private HashMap<String, JToggleButton> buttonIndex;
    private static final String SHIP_2 = "/images/ship2.png";
    private static final String SHIP_3 = "/images/ship3.png";
    private static final String SHIP_4 = "/images/ship4.png";
    private static final String SHIP_5 = "/images/ship5.png";

    public ShipDrawer(HashMap<String, JToggleButton> buttonIndex) {
        this.buttonIndex = buttonIndex;
    }

    public void drawCompleteShip(List<String> shipLocations) {
        if (shipLocations.isEmpty() || shipLocations.get(0).equals("/")) {
            return;
        }

        int shipSize = shipLocations.size();
        boolean horizontal = isHorizontalShip(shipLocations);

        try {
            String imagePath = getShipImagePath(shipSize);
            BufferedImage originalImage = ImageIO.read(getClass().getResource(imagePath));

            JToggleButton firstButton = buttonIndex.get(shipLocations.get(0));
            int buttonSize = firstButton.getWidth();

            System.out.println("buttonSize: " + buttonSize + " shipSize: " + shipSize);

            // Xử lý ảnh gốc
            BufferedImage processedImage;
            if (horizontal) {
                // Trường hợp ngang - giữ nguyên như cũ vì đang hoạt động tốt
                processedImage = new BufferedImage(
                        buttonSize * shipSize, buttonSize,
                        BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D g2d = processedImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(originalImage, 0, 0, buttonSize * shipSize, buttonSize, null);
                g2d.dispose();
            } else {
                // Trường hợp dọc - xử lý mới
                // Đầu tiên scale ảnh theo kích thước ngang
                BufferedImage scaledImage = new BufferedImage(
                        buttonSize * shipSize, buttonSize,
                        BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D g2d = scaledImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(originalImage, 0, 0, buttonSize * shipSize, buttonSize, null);
                g2d.dispose();

                // Sau đó xoay 90 độ
                processedImage = new BufferedImage(
                        buttonSize, buttonSize * shipSize,
                        BufferedImage.TYPE_INT_ARGB
                );
                g2d = processedImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                // Xoay quanh trung tâm
                AffineTransform at = new AffineTransform();
                at.translate(buttonSize / 2, buttonSize * shipSize / 2);
                at.rotate(-Math.PI / 2);
                at.translate(-buttonSize * shipSize / 2, -buttonSize / 2);

                g2d.setTransform(at);
                g2d.drawImage(scaledImage, 0, 0, null);
                g2d.dispose();
            }

            // Vẽ từng phần lên buttons
            for (int i = 0; i < shipLocations.size(); i++) {
                String location = shipLocations.get(i);
                JToggleButton button = buttonIndex.get(location);

                BufferedImage buttonImage = new BufferedImage(
                        buttonSize, buttonSize,
                        BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D g2d = buttonImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (horizontal) {
                    g2d.drawImage(processedImage,
                            0, 0, buttonSize, buttonSize,
                            i * buttonSize, 0, (i + 1) * buttonSize, buttonSize,
                            null);
                } else {
                    g2d.drawImage(processedImage,
                            0, 0, buttonSize, buttonSize,
                            0, i * buttonSize, buttonSize, (i + 1) * buttonSize,
                            null);
                }
                g2d.dispose();

                button.setIcon(new ImageIcon(buttonImage));
                button.setSelected(true);
                button.setEnabled(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to dot only if image loading fails
            for (String location : shipLocations) {
                JToggleButton button = buttonIndex.get(location);
                button.setIcon(new ImageIcon(getClass().getResource("/images/dot.png")));
                button.setSelected(true);
                button.setEnabled(false);
            }
        }
    }

    private boolean isHorizontalShip(List<String> locations) {
        if (locations.size() < 2) {
            return true;
        }
        return locations.get(0).charAt(0) == locations.get(1).charAt(0);
    }

    private String getShipImagePath(int size) {
        switch (size) {
            case 2:
                return SHIP_2;
            case 3:
                return SHIP_3;
            case 4:
                return SHIP_4;
            case 5:
                return SHIP_5;
            default:
                return "/images/dot.png";
        }
    }
}
