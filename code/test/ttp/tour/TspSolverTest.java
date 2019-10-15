package ttp.tour;


import org.junit.Assert;
import org.junit.Test;


public class TspSolverTest {

	@Test
	public void testSolverName() {

		
		Assert.assertEquals("/concorde", "/" + TspSolver.SolverName.CONCORDE.cmd());
		Assert.assertEquals("con", TspSolver.SolverName.CONCORDE.type());
		
		Assert.assertEquals("/linkern", "/" + TspSolver.SolverName.LKH.cmd());
		Assert.assertEquals("lkh", TspSolver.SolverName.LKH.type());
	}
	
	@Test
	public void testInverOver(){
		TspSolver tspSolver = new TspSolver();
		
		Assert.assertEquals(2613, tspSolver.solve("experiments/a280/a280.tsp", "experiments/a280/a280.tsp.test.inv.tour", TspSolver.SolverName.INVEROVER));
	
	}
	
	@Test
	public void testSolve() {
		TspSolver tspSolver = new TspSolver();
		
		Assert.assertEquals(2613, tspSolver.solve("experiments/a280/a280.tsp", "experiments/a280/a280.tsp.test.lkh2.tour", TspSolver.SolverName.LKH2));
		Assert.assertEquals(2613, tspSolver.solve("experiments/a280/a280.tsp", "experiments/a280/a280.tsp.test.lkh.tour", TspSolver.SolverName.LKH));
		Assert.assertEquals(2613, tspSolver.solve("experiments/a280/a280.tsp", "experiments/a280/a280.tsp.test.aco.tour", TspSolver.SolverName.ACO));
		Assert.assertEquals(2613, tspSolver.solve("experiments/a280/a280.tsp", "experiments/a280/a280.tsp.test.con.tour", TspSolver.SolverName.CONCORDE));
		
	}

}
