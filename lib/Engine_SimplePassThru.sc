Engine_SimplePassThru : NornsEngine {

	var amp=0;
	var <synth;

	// this is called when the engine is actually loaded by a script.
	// you can assume it will be called in a Routine,
	//  so you can use e.g. Server.sync and (time).wait methods.
	alloc {
		var server = Norns.server;
		// a synthdef that simply reads and writes a stereo signal.
		SynthDef(\adc_patch_stereo, {|amp=0 |
			// read 2 channels from the JACK input ports
			var sound = SoundIn.ar(0, 2);
			// write to the JACK output ports.
			// `sound` is a 2-channel signal at this point.
			Out.ar(0, sound*amp);
		}).send(server);

		// this tells the enclosing thread to pause,
		// until the server is finished processing all pending requests.
		server.sync;

		synth = Synth.new(\passThru, [\amp, 0], server);

		// this is how you add "commands",
		// which is how the lua interpreter controls the engine.
		// the format string is analogous to an OSC message format string,
		// and the 'msg' argument contains data.
		
		this.addCommand("test", "ifs", {|msg|
			msg.postln;
		});

		this.addCommand("amp", "f", {|msg|
			synth.set(\amp, msg[1]);
		});
	}

	free {
		// here you should free resources (e.g. Synths, Buffers &c)
		// and stop processes (e.g. Routines, Tasks &c)
		synth.free;
	}

} 