# My City
# my-city

Brief project README for the "my-city" software engineering project. This repository contains analysis material and artifacts for a university course project; development is ongoing.

## Project summary

my-city is a student software engineering project that models and documents a city-related application (requirements, use cases and architecture artifacts). The repository currently contains analysis and design documents — the implementation is not yet present.

Goals:
- Collect and formalise requirements and use cases for the system
- Produce architecture and design artifacts (UML diagrams, user stories)
- Move from analysis to implementation in a future phase

Status: Under construction — analysis and design phase. See the "Progress & next steps" section below.

## Repository layout

Top-level folders (current):

- `Anforderungsanalyse/` — requirements analysis (in German). Contains functional and non-functional requirements, acceptance criteria, and related documents.
- `Use Case Diagramm/` — use case diagrams and related UML artifacts.

Additions planned:

- `src/` — implementation sources (to be created)
- `docs/` — generated documentation and design notes
- `tests/` — automated tests
- `contributing.md` — contribution guide

If you add files, please follow the agreed naming conventions and keep design artifacts in the `docs/` or dedicated UML folders.

## How to read and use this repository

- Start in `Anforderungsanalyse/` to review the problem scope and acceptance criteria.
- Open diagrams in `Use Case Diagramm/` to understand user interactions and system boundaries.
- If you plan to implement features, create a new `feature/<short-name>` branch and add an entry to the project board or the `TODO.md` backlog.

No build or runtime environment is currently defined; this repository is primarily a documentation and analysis workspace.

## Contribution guide (short)

1. Fork or create a branch for your work: `feature/<your-topic>`.
2. Add or update analysis/design artifacts in their respective folders.
3. Open a merge request (or pull request) describing the change, motivation and any open questions.
4. For code contributions (future): include tests and a short README for the new module.

Before contributing analysis documents, discuss major changes in issues or in-class with the project lead.

## Progress & next steps

Current progress:

- Requirements documented (in progress).
- Use case diagrams collected.
- No source code yet.

Next steps (recommended):

1. Finalise and review the requirements (acceptance criteria, use cases).
2. Create `docs/` with a short architecture overview (context diagram, component sketch).
3. Create an initial `src/` skeleton (choose technology stack — e.g., Java / Spring Boot, Node.js, or similar) and add a minimal README for the implementation.
4. Add a `CONTRIBUTING.md`, `LICENSE` and a project `TODO.md` backlog.
5. If appropriate, add CI configuration for linting/tests once implementation begins.

## Assumptions

- This repository is primarily for coursework: documents and UML artifacts are the priority for the current semester.
- Language: many artifacts are in German; keep filenames and a brief English summary so non-German-speaking collaborators can follow.

## Contact and coordination

For team coordination, use the project issue tracker or the group chat used by the course. Put single-line summaries in issues and link related diagrams or requirement documents.

## Quick TODO for maintainers

- [ ] Finalise requirements in `Anforderungsanalyse/`
- [ ] Add `CONTRIBUTING.md` and `LICENSE`
- [ ] Create `docs/` with architecture sketch
- [ ] Decide target implementation stack and create `src/` skeleton

---

If you'd like, I can also:

- produce a ready-to-commit `CONTRIBUTING.md` and `LICENSE` draft
- convert any hand-drawn diagrams into SVG/PlantUML files
- scaffold a minimal `src/` skeleton in a language of your choice

Tell me which of the above you'd like next.
