package maze.gui.mazeeditor;

import java.awt.Graphics2D;
import java.awt.Point;

import maze.Main;
import maze.gui.CellSize;

/**
 * This class provides a corner shaped MazeTemplate that can be grown or shrunk
 * by increasing the length of its sides.
 * @author Johnathan Smith
 */
public class GappedTemplate extends MazeTemplate
{
   private static final int MIN_SIZE = 2;
   private int mSize = MIN_SIZE;
   private TemplatePeg[] mCenters;
   private Point[] mCenterPoints;
   private boolean mVert = true;
   private CellSize mLastSize = new CellSize(4,4,2,2,0,0);


   public GappedTemplate()
   {
      this.mIcon = Main.getImageResource("gui/mazeeditor/images/Gapped.png");
      this.mDesc = "Gapped Line";
      updateTemplate();
      mCenterPoints = new Point[]{new Point(0,0)};
   }

   @Override
   public TemplatePeg[] getCenterPegs()
   {
      return mCenters;
   }

   @Override
   public Point[] getCenterPoints(CellSize size)
   {
      if (mLastSize.compareTo(size) != 0)
         generatePoints(size);
      return mCenterPoints;
   }

   @Override
   public void updatePosition(Point p, CellSize size)
   {
      if (mCenterPoints.length != mCenters.length ||
          mLastSize.compareTo(size) != 0)
      {
         mCenterPoints[0] = (Point)p.clone();
         generatePoints(size);
      }
      refreshPoints(p);
   }

   @Override
   public void nextOrientation()
   {
      mVert = !mVert;
      updateTemplate();
      generatePoints(mLastSize);
   }

   @Override
   public void grow()
   {
      mSize++;
      updateTemplate();
      generatePoints(mLastSize);
   }

   @Override
   public void shrink()
   {
      if (mSize > MIN_SIZE)
      {
         mSize--;
         updateTemplate();
         generatePoints(mLastSize);
      }
   }

   @Override
   public void draw(Graphics2D g, CellSize size)
   {
      if (mCenterPoints.length != mCenters.length ||
          mLastSize.compareTo(size) != 0)
         generatePoints(size);

      super.draw(g, size);
   }

   @Override
   public void reset()
   {
      mVert = true;
      mSize = MIN_SIZE;
      updateTemplate();
   }

   private void updateTemplate()
   {
      mCenters = new TemplatePeg[mSize*2];

      TemplatePeg first, second;
      TemplateWall newWall;

      for (int i = 0; i < mCenters.length; i+=2)
      {
         if (mVert)
         {
            first = new TemplatePeg();
            second = new TemplatePeg();
            newWall = new TemplateWall();
            first.top = second.bottom = newWall;
            newWall.mRightTop = second;
            newWall.mLeftBottom = first;
            mCenters[i] = first;
         }
         else
         {
            first = new TemplatePeg();
            second = new TemplatePeg();
            newWall = new TemplateWall();
            first.left = second.right = newWall;
            newWall.mLeftBottom = second;
            newWall.mRightTop = first;
         }
         mCenters[i] = first;
         mCenters[i+1] = second;
      }
   }

   private void generatePoints(CellSize size)
   {
      mLastSize = size;
      Point c = mCenterPoints[0];
      if (mCenters.length != mCenterPoints.length)
         mCenterPoints = new Point[mCenters.length];

      int count = 0;

      for(int i = 0; i < mCenterPoints.length; i+=2)
      {
         int diffX = 0, diffY = 0;

         if ((i/2)%2 == 1)
         {
            if (mVert)
               diffY = (size.getCellHeight()+size.getWallHeight())*count;
            else
               diffX = -(size.getCellWidth()+size.getWallWidth())*count;
            
         }
         else
         {
            if (mVert)
               diffY = -(size.getCellHeight()+size.getWallHeight())*count;
            else
               diffX = (size.getCellWidth()+size.getWallWidth())*count;
            count+=2;
         }

         Point np1 = new Point(c.x+diffX,c.y+diffY);
         Point np2 = new Point(np1.x,np1.y);

        if (mVert)
            np2.y -= size.getCellHeight()+size.getWallHeight();
        else
            np2.x -= size.getCellWidth()+size.getWallWidth();

         mCenterPoints[i] = np1;
         mCenterPoints[i+1] = np2;
      }
   }

   private void refreshPoints(Point p)
   {
      int diffX = p.x-mCenterPoints[0].x, diffY = p.y-mCenterPoints[0].y;
      for (int i = 0; i < mCenterPoints.length; i++)
      {
         mCenterPoints[i].x += diffX;
         mCenterPoints[i].y += diffY;
      }
   }
}
