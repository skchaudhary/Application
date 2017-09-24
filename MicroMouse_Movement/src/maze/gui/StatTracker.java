/**
 * 
 */
package maze.gui;

import java.awt.Dimension;
import java.util.List;
import java.util.Set;

import maze.ai.RobotBase;
import maze.ai.RobotController;
import maze.ai.RobotStep;
import maze.model.MazeCell;
import maze.model.MazeModel;
import maze.model.RobotModel;
import maze.model.RobotModelMaster;

/**
 * @author Vincent Frey
 */
public class StatTracker
{

   /**
    * @param args
    */
   private int totalSquaresTraversed;
   private int totalTurnsTaken;
   private int firstRunSquaresTraversed;
   private int firstRunTurnsTaken;
   private int bestRunSquaresTraversed;
   private int bestRunTurnsTaken;
   private int bestRunTotalSquaresTraversed;
   private int bestRunTotalTurnsTaken;
   private int previousRunTotalSquaresTraversed;
   private int previousRunTotalTurnsTaken;
   private int currentRunSquaresTraversed;
   private int currentRunTurnsTaken;
   private boolean[][] explored;
   public static int USELESS = -1;
   public static int HOPELESS = 2000;

   private RobotBase algorithm;
   private RobotModelMaster mouse;
   private Dimension mazeSize;

   private RobotController controller;

   /**
    * This constructor requires an algorithm and a mouse. It will then determine
    * a handful of relevant statistics for the user to access
    */
   public StatTracker(RobotBase algorithm, RobotModelMaster mouse)
   {
      this.reload(algorithm, mouse);
   }

   /**
    * This function requires an algorithm and a mouse. It will then determine a
    * handful of relevant statistics for the user to access
    */
   public void reload(RobotBase algorithm, RobotModelMaster mouse)
   {
      this.algorithm = algorithm;
      this.mouse = mouse;

      this.controller = new RobotController(new MazeModel(mouse.getMazeSize().width,
                                                          mouse.getMazeSize().height), algorithm);

      this.initialize();
      this.recompute();
   }

   /**
    * This function prepares the mouse and algorithm to get ready to start a
    * run.
    */
   private void initialize()
   {
      this.controller.initialize();

      mazeSize = mouse.getMazeSize();
      explored = new boolean[mazeSize.width][mazeSize.width];
      for (int i = 0; i < mazeSize.width; i++)
      {
         for (int j = 0; j < mazeSize.height; j++)
         {
            explored[i][j] = false;
         }
      }
      totalSquaresTraversed = 0;
      totalTurnsTaken = 0;
      firstRunSquaresTraversed = USELESS;
      firstRunTurnsTaken = USELESS;
      bestRunSquaresTraversed = USELESS;
      bestRunTurnsTaken = USELESS;
      bestRunTotalSquaresTraversed = USELESS;
      bestRunTotalTurnsTaken = USELESS;
      previousRunTotalSquaresTraversed = 0;
      previousRunTotalTurnsTaken = 0;

      algorithm.setRobotLocation(new RobotModel(mouse));
      algorithm.initialize();
   }

   /**
    * This function simulates a run through the maze for the mouse and
    * algorithm.
    */
   private void recompute()
   {
      setExplored();
      trackARun();
      if (totalSquaresTraversed == HOPELESS)
      {
         if (currentRunSquaresTraversed != HOPELESS)
         { //Just in case the mouse makes it to the center but not back
            firstRunSquaresTraversed = currentRunSquaresTraversed;
            firstRunTurnsTaken = currentRunTurnsTaken;
            bestRunSquaresTraversed = currentRunSquaresTraversed;
            bestRunTurnsTaken = currentRunTurnsTaken;
            bestRunTotalSquaresTraversed = currentRunSquaresTraversed;
            bestRunTotalTurnsTaken = currentRunTurnsTaken;
         }
         return;
      }

      firstRunSquaresTraversed = currentRunSquaresTraversed;
      firstRunTurnsTaken = currentRunTurnsTaken;

      do
      {
         bestRunSquaresTraversed = currentRunSquaresTraversed;
         bestRunTurnsTaken = currentRunTurnsTaken;
         bestRunTotalSquaresTraversed = previousRunTotalSquaresTraversed +
                                        currentRunSquaresTraversed;
         bestRunTotalTurnsTaken = previousRunTotalTurnsTaken + currentRunTurnsTaken;
         previousRunTotalSquaresTraversed = totalSquaresTraversed;
         previousRunTotalTurnsTaken = totalTurnsTaken;
         trackARun();
      }
      while (bestRunSquaresTraversed > currentRunSquaresTraversed);
   }

   private void trackARun()
   {

      currentRunSquaresTraversed = 0;
      currentRunTurnsTaken = 0;

      //      while (!this.controller.isRobotDone() && !this.controller.getRobotModelMaster().isAtCenter())
      //      {
      //         if (this.controller.nextStep().isTurn())
      //         {
      //            totalTurnsTaken++;
      //            currentRunTurnsTaken++;
      //         }
      //         else
      //         {
      //            totalSquaresTraversed++;
      //            currentRunSquaresTraversed++;
      //            setExplored();
      //         }
      //      }
      //
      //      while (!this.controller.isRobotDone() && !this.controller.getRobotModelMaster().isAtStart())
      //      {
      //         if (this.controller.nextStep().isTurn())
      //         {
      //            totalTurnsTaken++;
      //         }
      //         else
      //         {
      //            totalSquaresTraversed++;
      //            setExplored();
      //         }
      //      }

      RobotStep nextStep;
      while ( (totalSquaresTraversed < HOPELESS) && (isAtCenter() == false))
      {
         nextStep = algorithm.nextStep();
         mouse.takeNextStep(nextStep);
         if (nextStep.isTurn())
         {
            totalTurnsTaken++;
            currentRunTurnsTaken++;
         }
         else
         {
            totalSquaresTraversed++;
            currentRunSquaresTraversed++;
            setExplored();
         }
      }

      while ( (totalSquaresTraversed < HOPELESS) && (isAtStart() == false))
      {
         nextStep = algorithm.nextStep();
         mouse.takeNextStep(nextStep);
         if (nextStep.isTurn())
         {
            totalTurnsTaken++;
         }
         else
         {
            totalSquaresTraversed++;
            setExplored();
         }
      }
   }

   private boolean isAtStart()
   {
      MazeCell here = mouse.getCurrentLocation();
      MazeCell start = new MazeCell(1, mazeSize.height);
      if (here.equals(start))
      {
         return true;
      }
      return false;
   }

   private boolean isAtCenter()
   {
      MazeCell here = mouse.getCurrentLocation();
      MazeCell goal1 = new MazeCell(mazeSize.width / 2, mazeSize.height / 2);
      MazeCell goal2 = goal1.plusX(1);
      MazeCell goal3 = goal1.plusY(1);
      MazeCell goal4 = goal3.plusX(1);
      if ( (here.equals(goal1)) ||
          (here.equals(goal2)) ||
          (here.equals(goal3)) ||
          (here.equals(goal4)))
      {
         return true;
      }
      return false;
   }

   private void setExplored()
   {
      MazeCell here = mouse.getCurrentLocation();
      explored[here.getX() - 1][here.getY() - 1] = true;
   }

   public int getTotalTraversed()
   {
      int unique = 0;
      for (int i = 0; i < mazeSize.width; i++)
      {
         for (int j = 0; j < mazeSize.height; j++)
         {
            if (explored[i][j] == true)
            {
               unique++;
            }
         }
      }
      return unique;
   }

   public int getFirstRunCells()
   {
      return firstRunSquaresTraversed;
   }

   public int getFirstRunTurns()
   {
      return firstRunTurnsTaken;
   }

   public int getBestRunCells()
   {
      return bestRunSquaresTraversed;
   }

   public int getBestRunTurns()
   {
      return bestRunTurnsTaken;
   }

   public int getThroughBestRunCells()
   {
      return bestRunTotalSquaresTraversed;
   }

   public int getThroughBestRunTurns()
   {
      return bestRunTotalTurnsTaken;
   }

   public Set<MazeCell> getAllUnexplored()
   {
      return mouse.getNonHistory();
   }

   public List<MazeCell> getFirstRun()
   {
      return mouse.getFirstRun();
   }

   public List<MazeCell> getBestRun()
   {
      return mouse.getBestRun();
   }
}
