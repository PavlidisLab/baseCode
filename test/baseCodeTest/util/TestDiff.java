package baseCodeTest.util;

/*
 * $Log$
 * Revision 1.1  2004/06/21 21:59:07  pavlidis
 * Added regression testing helpers; I put a Diff class I found in our project, perhaps not a good idea, I'll fix later. Tests added. Introduced Graph and GraphNode interfaces. More test data files. Graph traversal uses LinkedHashMaps or LInkedHashSets to ensure traversal order is consistent from run to run.
 *
 * Revision 1.2  2004/01/29 02:35:35  stuart
 * Test for out of bounds exception in UnifiedPrint.print_hunk.
 * Add setOutput() to DiffPrint.Base.
 *
 * Revision 1.1  2003/05/29 20:22:40  stuart
 * Test EditMask.getFix()
 *
 */


import junit.framework.TestCase;
import java.io.StringWriter;
import baseCode.util.Diff;
import baseCode.util.DiffPrint;

/** Test Diff behavior.
  @author Stuart D. Gathman
  Copyright (C) 2002 Business Management Systems, Inc.
 */
public class TestDiff extends TestCase {

  private static String[] f1 = { "hello" };
  private static String[] f2 = { "hello", "bye" };


  public TestDiff(String name) { super(name); }

  public void testReverse() {
    Diff diff = new Diff(f1,f2);
    Diff.change script = diff.diff_2(true);
    assertTrue(script != null);
    assertTrue(script.link == null);
  }

  private static String[] test1 = {
    "aaa","bbb","ccc","ddd","eee","fff","ggg","hhh","iii"
  };
  private static String[] test2 = {
    "aaa","jjj","kkk","lll","bbb","ccc","hhh","iii","mmm","nnn","ppp"
  };

  /** Test context based output.  Changes past the end of old file
    were causing an array out of bounds exception.
    Submitted by Cristian-Augustin Saita and Adam Rabung.
   */
  public void testContext() {
    Diff diff = new Diff(test1,test2);
    Diff.change script = diff.diff_2(false);
    DiffPrint.Base p = new DiffPrint.UnifiedPrint(test1,test2);
    StringWriter wtr = new StringWriter();
    p.setOutput(wtr);
    //p.print_header("test1","test2");
    p.print_script(script);
    /* FIXME: when DiffPrint is updated to diff-2.7, testfor expected
       output in wtr.toString(). diff-1.15 does not combine adjacent
       changes when they are close together. */
  }

}
