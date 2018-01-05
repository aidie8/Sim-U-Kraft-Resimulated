package ashjack.simukraftreloaded.blocks.functionality;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidMilk extends Fluid {

	public FluidMilk() {
		super("fluidMilk");

		setDensity(10); // How thick the fluid is, affects movement inside the liquid.
		setViscosity(1000); // How fast the fluid flows.
		FluidRegistry.registerFluid(this); // Registering inside it self, keeps things neat :)
	}

}
