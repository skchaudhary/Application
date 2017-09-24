/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maze.gui.mazeeditor;

/**
 *
 * @author Johnathan Smith
 */
public class TemplatePeg implements Comparable<TemplatePeg>
{
   public TemplateWall top = null;
   public TemplateWall bottom = null;
   public TemplateWall left = null;
   public TemplateWall right = null;

   @Override
   public int compareTo(TemplatePeg o)
   {
      if (left == o.left && right == o.right &&
          bottom == o.bottom && top == o.top)
         return 0;
      else if (left != null)
      {
         if (o.left != null)
            return left.hashCode()-o.left.hashCode();
         return 1;
      }
      else if (o.left != null)
         return -1;
      else if (right != null)
      {
         if (o.right != null)
            return right.hashCode()-o.right.hashCode();
         return 1;
      }
      else if (o.right != null)
         return -1;
      else if (bottom != null)
      {
         if (o.bottom != null)
            return bottom.hashCode()-o.bottom.hashCode();
         return 1;
      }
      else if (o.bottom != null)
         return -1;
      else if (top != null)
      {
         if (o.top != null)
            return top.hashCode()-o.top.hashCode();
         return 1;
      }
      else if (o.top != null)
         return -1;
      else
         return 0;
   }

   @Override
   protected Object clone() throws CloneNotSupportedException
   {
      TemplatePeg cloned = new TemplatePeg();
      cloned.top = top;
      cloned.bottom = bottom;
      cloned.left = left;
      cloned.right = right;
      return cloned;
   }

}
