export interface TeamVisual {
  logo: string;
  primary: string;
  secondary: string;
  accent: string;
}

const FALLBACK_VISUAL: TeamVisual = {
  logo: 'https://resources.premierleague.com/premierleague/badges/70/t1.png',
  primary: '#334155',
  secondary: '#0f172a',
  accent: '#cbd5e1'
};

const TEAM_VISUALS: Record<string, TeamVisual> = {
  arsenal: { logo: plBadge(3), primary: '#EF0107', secondary: '#9C0000', accent: '#FFFFFF' },
  astonvilla: { logo: plBadge(7), primary: '#670E36', secondary: '#95BFE5', accent: '#FEE505' },
  bournemouth: { logo: plBadge(91), primary: '#DA291C', secondary: '#000000', accent: '#FFFFFF' },
  brentford: { logo: plBadge(94), primary: '#E30613', secondary: '#202020', accent: '#FFFFFF' },
  brighton: { logo: plBadge(36), primary: '#0057B8', secondary: '#FFFFFF', accent: '#FFCD00' },
  burnley: { logo: plBadge(90), primary: '#6C1D45', secondary: '#99D6EA', accent: '#F8E71C' },
  chelsea: { logo: plBadge(8), primary: '#034694', secondary: '#001489', accent: '#FFFFFF' },
  crystalpalace: { logo: plBadge(31), primary: '#1B458F', secondary: '#C4122E', accent: '#FFFFFF' },
  everton: { logo: plBadge(11), primary: '#003399', secondary: '#1E2A78', accent: '#FFFFFF' },
  fulham: { logo: plBadge(54), primary: '#FFFFFF', secondary: '#111827', accent: '#CC0000' },
  ipswich: { logo: plBadge(40), primary: '#0057B8', secondary: '#1D3C88', accent: '#FFFFFF' },
  ipswichtown: { logo: plBadge(40), primary: '#0057B8', secondary: '#1D3C88', accent: '#FFFFFF' },
  liverpool: { logo: plBadge(14), primary: '#C8102E', secondary: '#8B0000', accent: '#00B2A9' },
  luton: { logo: plBadge(102), primary: '#F78F1E', secondary: '#0057A8', accent: '#FFFFFF' },
  mancity: { logo: plBadge(43), primary: '#6CABDD', secondary: '#1C2C5B', accent: '#FFFFFF' },
  manunited: { logo: plBadge(1), primary: '#DA291C', secondary: '#7A0000', accent: '#FBE122' },
  newcastle: { logo: plBadge(4), primary: '#000000', secondary: '#FFFFFF', accent: '#41B6E6' },
  nottmforest: { logo: plBadge(17), primary: '#DD0000', secondary: '#A30000', accent: '#FFFFFF' },
  sheffieldutd: { logo: plBadge(49), primary: '#EE2737', secondary: '#111827', accent: '#FFFFFF' },
  southampton: { logo: plBadge(20), primary: '#D71920', secondary: '#FFFFFF', accent: '#000000' },
  spurs: { logo: plBadge(6), primary: '#132257', secondary: '#FFFFFF', accent: '#5BA3D0' },
  tottenham: { logo: plBadge(6), primary: '#132257', secondary: '#FFFFFF', accent: '#5BA3D0' },
  westham: { logo: plBadge(21), primary: '#7A263A', secondary: '#1BB1E7', accent: '#F3D459' },
  wolves: { logo: plBadge(39), primary: '#FDB913', secondary: '#231F20', accent: '#FFFFFF' },
  leicester: { logo: plBadge(13), primary: '#0053A0', secondary: '#003090', accent: '#FDBE11' }
};

function plBadge(id: number): string {
  return `https://resources.premierleague.com/premierleague/badges/70/t${id}.png`;
}

function normalizeTeam(team: string): string {
  return (team || '')
    .toLowerCase()
    .replace(/[^a-z0-9]/g, '')
    .replace('aston', 'astonvilla')
    .replace('crystalp', 'crystalpalace')
    .replace('leics', 'leicestercity')
    .replace('leic', 'leicestercity')
    .replace('lutontown', 'luton')
    .replace('newcastleu', 'newcastle')
    .replace('nottinghamf', 'nottmforest')
    .replace('westhamu', 'westham')
    .replace('wolverhampton', 'wolves')
    .replace('sheffieldun', 'sheffieldutd')
    .replace('sheffieldut', 'sheffieldutd')
    .replace('manchesterunitedfootballclub', 'manunited')
    .replace('manchesterunitedfc', 'manunited')
    .replace('manchestercity', 'mancity')
    .replace('manchestercity', 'mancity')
    .replace('manchesterutd', 'manunited')
    .replace('manutd', 'manunited')
    .replace('manchesterunited', 'manunited')
    .replace('ipswichtownfootballclub', 'ipswich')
    .replace('ipswichtownfc', 'ipswich')
    .replace('ipswichtown', 'ipswich')
    .replace('newcastleunited', 'newcastle')
    .replace('tottenhamhotspur', 'spurs')
    .replace('nottinghamforest', 'nottmforest');
}

export function getTeamVisual(team: string): TeamVisual {
  const key = normalizeTeam(team);
  return TEAM_VISUALS[key] || FALLBACK_VISUAL;
}

export function getTeamLogo(team: string): string {
  return getTeamVisual(team).logo;
}

export function getTeamJersey(team: string): string {
  const visual = getTeamVisual(team);
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="${visual.primary}"/>
      <stop offset="100%" stop-color="${visual.secondary}"/>
    </linearGradient>
  </defs>
  <path d="M30 22 L44 16 L60 25 L76 16 L90 22 L98 46 L84 56 L84 101 L36 101 L36 56 L22 46 Z" fill="url(#g)" stroke="rgba(255,255,255,0.35)" stroke-width="3"/>
  <rect x="49" y="20" width="22" height="12" rx="5" fill="${visual.accent}" opacity="0.85"/>
  <path d="M40 44 H80" stroke="${visual.accent}" stroke-width="4" opacity="0.65"/>
  <circle cx="60" cy="66" r="7" fill="${visual.accent}" opacity="0.85"/>
</svg>`;

  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
}

export function getTeamCardStyle(team: string): { background: string; borderColor: string } {
  const visual = getTeamVisual(team);
  return {
    background: `linear-gradient(135deg, ${visual.primary}, ${visual.secondary})`,
    borderColor: 'rgba(255,255,255,0.25)'
  };
}

export function getTeamJerseyWithLogo(team: string): { jersey: string; logo: string } {
  return {
    jersey: getTeamJersey(team),
    logo: getTeamLogo(team)
  };
}
