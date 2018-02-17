package pflp.matt;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import pflp.Instance;
import pflp.PFLPApp;
import pflp.PointFeature;
import pflp.Solution;
import pflp.search.ForceDirectedLabeling;
import pflp.search.SearchThread;

public class LabelMaker {

  List<Point2D> points;
  List<Text> labels;
  List<PointFeature> pfs;

  public LabelMaker(List<Point2D> points, List<Text> labels) {

    if (points.size() != labels.size()) {
      throw new IllegalArgumentException("Length of points must match length of labels");
    }

    this.points = points;
    this.labels = labels;

  }

  public List<Point2D> placeLabels() {
    Vector<PointFeature> pointFeatures = constructPointFeatures();
    Solution soln = runLabelPlacementAlgorithm(pointFeatures);
    return processSolution(soln);
  }

  private List<Point2D> processSolution(Solution soln) {
    List<Point2D> finalPoints = new ArrayList<Point2D>();

    pflp.Label[] placedLabels = soln.getLabels();

    for (int j = 0; j < placedLabels.length; j++) {

      pflp.Label label = placedLabels[j];
      if (label.getUnplacable()) {
        finalPoints.add(null);
        continue;
      }

      //java.awt.geom.Point2D.Double point  = label.getCenter();
      //Point2D pointfx = new Point2D(point.getX(),point.getY());

      Point2D pointfx = new Point2D(label.getOffsetHorizontal(), label.getOffsetVertical());
      finalPoints.add(pointfx);

    }

    return finalPoints;
  }

  private Solution runLabelPlacementAlgorithm(Vector<PointFeature> pointFeatures) {
    Instance instance = new Instance(pointFeatures, "test");

    PFLPApp app = new PFLPApp();
    PFLPApp.instance = instance;
    PFLPApp.solution = null;

    SearchThread algorithm = new ForceDirectedLabeling();
    algorithm.run();

    Solution soln = app.solution;
    return soln;
  }

  private Vector<PointFeature> constructPointFeatures() {

    Vector<PointFeature> pointFeatures = new Vector<PointFeature>();

    for (int i = 0; i < labels.size(); i++) {

      Text label = labels.get(i);
      Point2D point = points.get(i);

      Bounds bounds = label.getLayoutBounds();

      double width = bounds.getWidth();
      double height = bounds.getHeight();
      double x = point.getX();
      double y = point.getY();

      Font font = label.getFont();

      PointFeature pf = new PointFeature(x, y, width, height, 1, label.getText(), font.getName(), (int) font.getSize());

      pointFeatures.add(pf);
    }

    this.pfs = pointFeatures;
    return pointFeatures;
  }

}
