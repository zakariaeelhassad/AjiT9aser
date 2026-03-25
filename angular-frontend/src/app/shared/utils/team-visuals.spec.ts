import {
  getTeamCardStyle,
  getTeamJersey,
  getTeamJerseyWithLogo,
  getTeamLogo,
  getTeamVisual
} from './team-visuals';
import { describe, expect, it } from 'vitest';

describe('team-visuals utils', () => {
  it('should map known aliases to the same club visual (Manchester United)', () => {
    const visualA = getTeamVisual('Manchester United');
    const visualB = getTeamVisual('manutd');

    expect(visualA.logo).toBe(visualB.logo);
    expect(visualA.primary).toBe(visualB.primary);
    expect(visualA.secondary).toBe(visualB.secondary);
  });

  it('should return fallback visual for unknown team', () => {
    const visual = getTeamVisual('Unknown Team FC');

    expect(visual.logo).toContain('/badges/70/t1.png');
    expect(visual.primary).toBe('#334155');
    expect(visual.secondary).toBe('#0f172a');
    expect(visual.accent).toBe('#cbd5e1');
  });

  it('should generate jersey data URI and consistent card style/logo payload', () => {
    const jersey = getTeamJersey('Arsenal');
    const logo = getTeamLogo('Arsenal');
    const style = getTeamCardStyle('Arsenal');
    const combined = getTeamJerseyWithLogo('Arsenal');

    expect(jersey.startsWith('data:image/svg+xml;utf8,')).toBe(true);
    expect(logo).toContain('/badges/70/t');
    expect(style.background.startsWith('linear-gradient(')).toBe(true);
    expect(style.borderColor).toBe('rgba(255,255,255,0.25)');

    expect(combined.jersey).toBe(jersey);
    expect(combined.logo).toBe(logo);
  });
});
