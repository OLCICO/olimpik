const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const COACH_ALICE = "coach_alice_123";
const COACH_BOB = "coach_bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read sessions", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("sessions").get());
});

test("Unauthenticated user: cannot create session", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("sessions").doc("sess_1").set({
      sessionId: "sess_1",
      branch: "Basketbol",
      hall: "Pendik",
      ageGroup: "U14",
      gender: "Erkek",
      date: "2026-10-26",
      startTime: "17:00",
      endTime: "18:30",
      coachId: "anon",
      status: "Aktif",
      createdAt: new Date(),
    })
  );
});

test("Authenticated coach: can create valid session", async () => {
  const aliceDb = testEnv.authenticatedContext(COACH_ALICE).firestore();
  await assertSucceeds(
    aliceDb.collection("sessions").doc("sess_alice").set({
      sessionId: "sess_alice",
      title: "U14 Basketbol Antrenmanı",
      branch: "Basketbol",
      hall: "Pendik Spor Kompleksi",
      ageGroup: "U14",
      gender: "Erkek",
      dayOfWeek: "Pazartesi",
      date: "2026-10-26",
      startTime: "17:00",
      endTime: "18:30",
      coachId: COACH_ALICE,
      coachName: "Koç Alice",
      status: "Aktif",
      statusNote: "",
      isMatch: false,
      createdAt: new Date(),
    })
  );
});

test("Authenticated coach: cannot create session with mismatched coachId", async () => {
  const aliceDb = testEnv.authenticatedContext(COACH_ALICE).firestore();
  await assertFails(
    aliceDb.collection("sessions").doc("sess_bad").set({
      sessionId: "sess_bad",
      branch: "Basketbol",
      hall: "Gebze Arena",
      ageGroup: "U16",
      gender: "Kız",
      date: "2026-10-26",
      startTime: "18:00",
      endTime: "19:30",
      coachId: COACH_BOB, // Mismatched coachId!
      status: "Aktif",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: can read sessions", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("sessions").doc("seed_sess").set({
      sessionId: "seed_sess",
      branch: "Voleybol",
      hall: "Şekerpınar Salonu",
      ageGroup: "U12",
      gender: "Kız",
      date: "2026-10-27",
      startTime: "10:00",
      endTime: "11:30",
      coachId: COACH_BOB,
      status: "Aktif",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(COACH_ALICE).firestore();
  await assertSucceeds(aliceDb.collection("sessions").doc("seed_sess").get());
});

test("Authenticated coach: can create announcement", async () => {
  const aliceDb = testEnv.authenticatedContext(COACH_ALICE).firestore();
  await assertSucceeds(
    aliceDb.collection("announcements").doc("ann_1").set({
      announcementId: "ann_1",
      title: "Antrenman Saati Güncellendi",
      message: "Cumartesi idmanı saat 11:00'e alındı.",
      type: "TIME_CHANGED",
      authorId: COACH_ALICE,
      authorName: "Koç Alice",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: can upvote weekly star player", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("weekly_players").doc("star_1").set({
      playerId: "star_1",
      name: "Ali Demir",
      category: "U14 Basketbol Erkek",
      branch: "Basketbol",
      achievement: "25 sayı, 8 asist",
      week: "2026-W43",
      votesCount: 10,
      nominatedBy: COACH_BOB,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(COACH_ALICE).firestore();
  await assertSucceeds(
    aliceDb.collection("weekly_players").doc("star_1").update({
      votesCount: 11,
      updatedAt: new Date(),
    })
  );
});
