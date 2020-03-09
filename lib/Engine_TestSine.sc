// NornsEngine_TestSine
// simplest possible test: a single, mono sinewave

// Inherit methods from NornsEngine
Engine_TestSine : NornsEngine {
	// Define a getter for the synth variable
	var <synth;

	// Define a class method when an object is created
	*new { arg doneCallback;
		// Return the object from the superclass (NornsEngine) .new method
		^super.new(doneCallback);
	}
	alloc {
		var server = Norns.server;
		// {}.play is a syntax shortcut which does two things:
		// (1) create a SynthDef from a function and send it to the target
		// (2) create a new Synth from the new SynthDef
		synth = {
			// define arguments to the function
			arg out, hz=220, amp=0.5, amplag=0.02, hzlag=0.01;
			// initialize two local vars for Lag'd amp and hz
			var amp_, hz_;
			// Allow Lag (Slew in modular jargon) for amplitude and frequency
			amp_ = Lag.ar(K2A.ar(amp), amplag);
			hz_ = Lag.ar(K2A.ar(hz), hzlag);
			// Create an output object with two copies of a SineOsc,
			// passing the Lag'd amp and frequency as args
			Out.ar(out, (SinOsc.ar(hz_) * amp_).dup);
		// Send the synth function to the server as a UGen graph.
		}.play(args: [\out, 0], target: server);

		// Export argument symbols as modulatable parameters
		// This could be extended to control the Lag time as additional params
		this.addCommand("hz", "f", { arg msg;
			synth.set(\hz, msg[1]);
		});

		this.addCommand("amp", "f", { arg msg;
			synth.set(\amp, msg[1]);
		});
	}
	// define a function that is called when the synth is shut down
	free {
		synth.free;
	}
}
