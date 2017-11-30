{
	wolfDen1: {
		width: 30,
		height: 30,
		dark: true,
		down: "wolfDen2",
		name: "Wolf's Den-1",
		caveCarvers: 0,
		boxCarvers: 0,
		roundCarvers: 1,
	},
	wolfDen2: {
		width: 120,
		height: 75,
		dark: false,
		water: 10,
		up: "wolfDen1",
		down: "wolfDen3",
		name: "Wolf's Den-2",
		caveCarvers: 2,
		roundCarvers: 3,
	},
	wolfDen3: {
		width: 100,
		height: 100,
		dark: true,
		doors: 10,
		doubleDoors: true,
		up: "wolfDen2",
		name: "Wolf's Den-3",
		caveCarvers: 3,
	}
}