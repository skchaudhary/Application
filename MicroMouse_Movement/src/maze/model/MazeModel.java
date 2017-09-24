package maze.model;

import java.awt.Dimension;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import maze.util.ListenerSubject;

/**
 * Represents all the walls of a This class extends
 * {@link maze.util.ListenerSubject<MazeCell>} which allows it to notify
 * listeners when a wall changes and provide the {@link maze.model.MazeCell}
 * that contains the changed wall.
 */
public class MazeModel extends ListenerSubject<MazeCell>
{
   /**
    * Number of columns or width of this maze.
    */
   private int width;

   /**
    * Number of rows or height of this maze.
    */
   private int height;

   private BitSet cwalls;
   private BitSet rwalls;
   private static final int DEFAULT_SIZE = 16; //A maze is generally 16 x 16
   public static final int NORTH = 0;
   public static final int EAST = 1;
   public static final int SOUTH = 2;
   public static final int WEST = 3;

   //Another constructor should be made to pass in a predetermined maze
   public MazeModel()
   {
      this(DEFAULT_SIZE, DEFAULT_SIZE);
   }

   /**
    * Copy constructor.
    * @param model The <code>MazeModel</code> to copy from.
    */
   public MazeModel(MazeModel model)
   {
      width = model.width;
      height = model.height;
      cwalls = (BitSet) model.cwalls.clone();
      rwalls = (BitSet) model.rwalls.clone();
   }

   /**
    * Construct a new maze model with the given dimensions.
    * @param width The width or number of columns of the new maze.
    * @param height The height or number of rows of the new maze.
    */
   public MazeModel(int width, int height)
   {
      this.width = width + width % 2;
      this.height = height + height % 2;
      rwalls = new BitSet(width * (height - 1));
      cwalls = new BitSet( (width - 1) * height);
      setWall(1, height, EAST);
   }

   public void setWall(int x, int y, int dir)
   {
      //Check for the boundary conditions
      //First make sure that we're in the maze
      if ( (x < 1) || (y < 1) || (x > width) || (y > height))
         return;
      //Second make sure that the wall isn't implied
      if ( ( (dir == NORTH) && (y == 1)) ||
          ( (dir == EAST) && (x == width)) ||
          ( (dir == SOUTH) && (y == height)) ||
          ( (dir == WEST) && (x == 1)))
         return;

      x--;
      y--;

      if (dir == NORTH)
         y--;
      if (dir == WEST)
         x--;

      if (dir == NORTH || dir == SOUTH)
      {
         if (!rwalls.get(y * width + x))
         {
            rwalls.set(y * width + x);
            super.notifyListeners(new MazeCell(x + 1, y + 1));
         }
      }
      else
      {
         if (!cwalls.get(x * height + y))
         {
            cwalls.set(x * height + y);
            super.notifyListeners(new MazeCell(x + 1, y + 1));
         }
      }

   }

   public boolean getWall(int x, int y, int dir)
   {
      //Check for the boundary conditions
      if ( ( (dir == NORTH) && (y == 1)) ||
          ( (dir == EAST) && (x == width)) ||
          ( (dir == SOUTH) && (y == height)) ||
          ( (dir == WEST) && (x == 1)))
         return true;

      if ( (x > width) || (y > height) || (x < 1) || (y < 1))
         return true;
      x--;
      y--;

      if (dir == NORTH)
         y--;
      if (dir == WEST)
         x--;

      if (dir == NORTH || dir == SOUTH)
         return rwalls.get(y * width + x);
      else
         return cwalls.get(x * height + y);
   }

   public void clearWall(int x, int y, int dir)
   {
      //Check for the boundary conditions
      if ( ( (dir == NORTH) && (y == 1)) ||
          ( (dir == EAST) && (x == width)) ||
          ( (dir == SOUTH) && (y == height)) ||
          ( (dir == WEST) && (x == 1)))
         return;

      if (x > width || y > height || x < 1 || y < 1)
         return;
      x--;
      y--;

      if (dir == NORTH)
         y--;
      if (dir == WEST)
         x--;

      if (dir == NORTH || dir == SOUTH)
      {
         if (rwalls.get(y * width + x))
         {
            rwalls.clear(y * width + x);
            super.notifyListeners(new MazeCell(x + 1, y + 1));
         }
      }
      else
      {
         if (cwalls.get(x * height + y))
         {
            cwalls.clear(x * height + y);
            super.notifyListeners(new MazeCell(x + 1, y + 1));
         }
      }
   }

   public boolean isLegal()
   {
      //There are three rules that must be upheld to be a valid maze
      //Rule 1: There is a wall next to the starting square
      if (!getWall(1, height, EAST) || getWall(1, height, NORTH))
         return false;
      //Rule 2: There is one and only one way into the center of the maze
      //Even though I've coded this to accept a variable size in the future
      //I shall still assume here that the total size will always be even      
      int row = height / 2, column = width / 2;
      if (!isCenterLegal())
         return false;

      //Offshoot of Rule 2 is that the center of the maze is an open peg
      if (!isCenterOpen())
         return false;

      //Rule 3: There must be at least one wall coming from every peg
      //Except for the lone center peg
      for (int i = 1; i <= width; i++)
      {
         for (int j = 1; j <= height; j++)
         {
            if ( (i == row && j == column) || isPegLegal(i, j))
               continue;
            return false;
         }
      }
      return true;
   }

   public void clearMaze()
   {
      rwalls.clear();
      cwalls.clear();
      setWall(1, height, EAST);
   }

   public boolean isPegLegal(int x, int y)
   {
      //This function is designed to give a lower-level view to the isLegal
      //method by telling about a specific peg

      //The peg location is based off of the cell that it is in the NORTHWEST
      //corner of.  Peg (1,1) is the only peg touching cell (1,1)
      if (x < 1 || y < 1 || x >= width || y >= height)
         return true;
      //Every peg should have at least one wall attached to it
      if (getWall(x, y, SOUTH) ||
          getWall(x, y, EAST) ||
          getWall(x + 1, y + 1, NORTH) ||
          getWall(x + 1, y + 1, WEST))
      {
         //There are only three cases with walls that must be checked
         //First is the starting square
         if ( (x == 1) && (y == height - 1))
         {
            if (getWall(1, height, EAST) && !getWall(1, height, NORTH))
               return true;
            else
               return false;
         }

         //Second is that it is not the center peg
         else if (x == (width / 2) && y == (height / 2))
            return isCenterLegal();

         //Third is the pegs surrounding the center square
         else if ( (x <= width / 2 && y <= height / 2) &&
                  (x >= width / 2 - 1 && y >= height / 2 - 1))
            return isCenterOpen();
         //There are no more reasons to check the walls
         else
            return true;
      }
      else
      {
         //The only exception to no wall is the center peg
         if (x == width / 2 && y == height / 2)
            return true;
         else
            return false;
      }
   }

   public boolean isCenterLegal()
   {
      int row = height / 2;
      int column = width / 2;
      if (getWall(column, row, SOUTH) ||
          getWall(column, row, EAST) ||
          getWall(column + 1, row, SOUTH) ||
          getWall(column, row + 1, EAST))
         return false;
      return true;
   }

   public boolean isCenterOpen()
   {
      int walls = 0;
      int row = height / 2;
      int column = width / 2;
      if (getWall(column, row, NORTH))
         walls++;
      if (getWall(column, row, WEST))
         walls++;
      if (getWall(column + 1, row, NORTH))
         walls++;
      if (getWall(column + 1, row, EAST))
         walls++;
      if (getWall(column, row + 1, WEST))
         walls++;
      if (getWall(column, row + 1, SOUTH))
         walls++;
      if (getWall(column + 1, row + 1, EAST))
         walls++;
      if (getWall(column + 1, row + 1, SOUTH))
         walls++;

      if (walls != 7)
         return false;
      return true;
   }

   /**
    * This function is basically isLegal with the added capability of returning
    * the peg locations that are bad.<br />
    * There are rules that must be upheld to be a valid maze.<br />
    * Rule 1: There is a wall next to the starting square.<br />
    * Rule 2: There is one and only one way into the center of the maze.<br />
    * Even though I've coded this to accept a variable size in the future I
    * shall still assume here that the total size will always be even.
    * @return A <code>set</code> of points where each point represents an
    *         illegal peg.
    */
   public Set<Point> illegalPegs()
   {
      TreeSet<Point> badPoints = new TreeSet<Point>(new PointCompare());

      int column = width / 2;
      int row = height / 2;

      if (!isCenterOpen())
      {
         badPoints.add(new Point(column - 1, row - 1));
         badPoints.add(new Point(column - 1, row));
         badPoints.add(new Point(column - 1, row + 1));
         badPoints.add(new Point(column, row - 1));
         badPoints.add(new Point(column, row + 1));
         badPoints.add(new Point(column + 1, row - 1));
         badPoints.add(new Point(column + 1, row));
         badPoints.add(new Point(column + 1, row + 1));
      } //All of the center pegs are connected

      //Offshoot of Rule 2 is that the center of the maze is an open peg
      if (!isCenterLegal())
         badPoints.add(new Point(column, row));

      //Rule 3: There must be at least one wall coming from every peg
      //Except for the lone center peg
      for (int i = 1; i <= width; i++)
      {
         for (int j = 1; j <= height; j++)
         {
            if (!isPegLegal(i, j))
               badPoints.add(new Point(i, j));
         }
      }

      return badPoints;
   }

   public void setSize(Dimension size)
   {
      width = size.width + size.width % 2;
      height = size.height + size.height % 2;
      rwalls = new BitSet(width * (height - 1));
      cwalls = new BitSet( (width - 1) * height);
   }

   /**
    * Get the size or dimension of this maze in columns by rows.
    * @return The current size of this maze model.
    */
   public Dimension getSize()
   {
      return new Dimension(width, height);
   }

   /**
    * Get an object that can then be checked or changed.
    * @param cell The cell containing the wall you want.
    * @param direction The direction of the wall you want from within the given
    *           cell.
    * @return A <code>MazeWall</code> of the specified location.
    */
   public MazeWall getWall(final MazeCell cell, final Direction direction)
   {
      return new MazeWall()
      {
         @Override
         public boolean isSet()
         {
            return getWall(cell.getX(), cell.getY(), direction.getIndex());
         }

         @Override
         public void set(boolean value)
         {
            if (value)
               setWall(cell.getX(), cell.getY(), direction.getIndex());
            else
               clearWall(cell.getX(), cell.getY(), direction.getIndex());
         }
      };
   }

   public void saveMaze(String filename, String name) throws IOException
   {
      //This is the formatting of the .MAZ files found online
      //This format is 4 times more inefficient than needs be
      //but it is a format that is already present
      if (name == null)
      {
         byte[] fileContents = new byte[256];
         for (int i = 1; i <= 16; i++)
         {
            for (int j = 16; j > 0; j--)
            {
               MazeCell cell = new MazeCell(i, j);
               byte cellWalls = 0;
               if (this.getWall(cell, Direction.North).isSet())
               {
                  cellWalls = 1;
               }
               if (this.getWall(cell, Direction.East).isSet())
               {
                  cellWalls += 2;
               }
               if (this.getWall(cell, Direction.South).isSet())
               {
                  cellWalls += 4;
               }
               if (this.getWall(cell, Direction.West).isSet())
               {
                  cellWalls += 8;
               }

               fileContents[16 * (i - 1) + (16 - j)] = cellWalls;
            }
         }

         //Now for the actual file i/o
         if (filename.toLowerCase().endsWith(".maz") == false)
            filename = filename + ".maz";

         FileOutputStream out = new FileOutputStream(filename);
         out.write(fileContents);
         out.close();
      } // if (name == null)
      else
      {
         if (filename.toLowerCase().endsWith(".mz2") == false)
            filename = filename + ".mz2";

         FileOutputStream out = new FileOutputStream(filename);
         DataOutputStream dis = new DataOutputStream(out);
         dis.writeUTF(name);
         dis.writeInt(width);
         dis.writeInt(height);

         int rwallSize = rwalls.size(), cwallSize = cwalls.size();
         int rwallArraySize = rwalls.size() / 8;
         if (rwalls.size() % 8 > 0)
            rwallArraySize++;
         int cwallArraySize = cwalls.size() / 8;
         if (cwalls.size() % 8 > 0)
            cwallArraySize++;
         byte[] rwallArray = new byte[rwallArraySize];
         byte[] cwallArray = new byte[cwallArraySize];
         for (int i = 0; i < rwallArray.length; i++)
            rwallArray[i] = 0;
         for (int i = 0; i < cwallArray.length; i++)
            cwallArray[i] = 0;

         int index = 0;

         for (int i = 0; i < rwallSize; i++)
         {
            rwallArray[index] |= ( (rwalls.get(i) ? 1 : 0) << (i % 8));
            if ( (i + 1) % 8 == 0)
               index++;
         }

         index = 0;

         for (int i = 0; i < cwallSize; i++)
         {
            cwallArray[index] |= ( (cwalls.get(i) ? 1 : 0) << (i % 8));
            if ( (i + 1) % 8 == 0)
               index++;
         }

         dis.write(rwallArray);
         dis.write(cwallArray);
         dis.close();
      }

   }

   public String loadMaze(InputStream in, boolean extended) throws IOException
   {
      if (extended)
      {
         DataInputStream dis = new DataInputStream(in);
         String name = dis.readUTF();
         setSize(new Dimension(dis.readInt(), dis.readInt()));

         int rwallSize = rwalls.size(), cwallSize = cwalls.size();
         for (int i = 0; i < rwallSize;)
         {
            byte bits = dis.readByte();
            for (int j = 0; j < 8 && i < rwallSize; j++, i++)
            {
               rwalls.set(i, (bits & 1) == 1);
               bits >>= 1;
            }
         }
         for (int i = 0; i < cwallSize;)
         {
            byte bits = dis.readByte();
            for (int j = 0; j < 8 && i < cwallSize; j++, i++)
            {
               cwalls.set(i, (bits & 1) == 1);
               bits >>= 1;
            }
         }
         return name;
      }
      else
      {
         //This is the formatting of the .MAZ files found online
         //This format is 4 times more inefficient than needs be
         //but it is a format that is already present
         byte[] fileContents = new byte[256];
         setSize(new Dimension(DEFAULT_SIZE, DEFAULT_SIZE));

         if (in.available() != 256)
            throw new IOException("Invalid format");

         in.read(fileContents);

         for (int i = 1; i <= 16; i++)
         {
            for (int j = 16; j > 0; j--)
            {
               byte cellWalls = fileContents[16 * (i - 1) + (16 - j)];
               if ( (cellWalls & 1) == 1)
                  this.setWall(i, j, NORTH);
               else
                  this.clearWall(i, j, NORTH);

               if ( (cellWalls & 2) == 2)
                  this.setWall(i, j, EAST);
               else
                  this.clearWall(i, j, EAST);

               if ( (cellWalls & 4) == 4)
                  this.setWall(i, j, SOUTH);
               else
                  this.clearWall(i, j, SOUTH);

               if ( (cellWalls & 8) == 8)
                  this.setWall(i, j, WEST);
               else
                  this.clearWall(i, j, WEST);
            } // for(int j=16; j>0; j--)
         } // for(int i=1; i<=16; i++)
         return null;
      }
   }

   public String loadMaze(String filename) throws FileNotFoundException,
                                                  IOException
   {

      //Now for the actual file i/o
      File file = new File(filename.toLowerCase());

      String name = filename;

      FileInputStream in = null;
      try
      {
         in = new FileInputStream(file);
      }
      catch (FileNotFoundException e)
      {
         System.out.println("first place");
         System.out.println(filename);
      }
      
      if (filename.endsWith(".mz2"))
         name = loadMaze(in, true);
      else
         loadMaze(in, false);

      try
      {
         in.close();
      }
      catch (IOException e){}
      
      return name;
   }

   @Override
   public Object clone()
   {
      MazeModel mm = new MazeModel();
      mm.width = width;
      mm.height = height;
      mm.rwalls = (BitSet) rwalls.clone();
      mm.cwalls = (BitSet) cwalls.clone();
      return mm;
   }

   /**
    * Used to store points in a Set.
    */
   private static class PointCompare implements Comparator<Point>
   {
      @Override
      public int compare(Point o1, Point o2)
      {
         if (o1.x == o2.x)
            if (o1.y == o2.y)
               return 0;
            else
               return o1.y - o2.y;
         else
            return o1.x - o2.x;
      }
   }

   /**
    * A <code>MazeWall</code> object represents the model for a single wall in a
    * maze. It has a method to check whether the wall is set/enabled or not. And
    * to change its status.
    */
   public static interface MazeWall
   {
      public boolean isSet();

      public void set(boolean value);
   }
}
