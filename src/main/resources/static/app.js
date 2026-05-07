// API Configuration
const API_BASE = ''; 
let currentUser = JSON.parse(localStorage.getItem('uniflix_user')) || null;

// State Management
let allMovies = [];
let favorites = [];
let profiles = [];
let isManagingProfiles = false;

// DOM Elements
const appContent = document.getElementById('app-content');
const loginSection = document.getElementById('login-section');
const profileSection = document.getElementById('profile-section');
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const profileList = document.getElementById('profile-list');

const movieGrid = document.getElementById('movie-grid');
const homeFavGrid = document.getElementById('home-favorites-grid');
const homeRecGrid = document.getElementById('home-recommendations-grid');
const favGrid = document.getElementById('favorites-grid');

const searchInput = document.getElementById('movie-search');
const syncBtn = document.getElementById('sync-btn');
const modal = document.getElementById('movie-modal');
const closeModal = document.querySelector('.close');

// Initial Load
document.addEventListener('DOMContentLoaded', () => {
    if (currentUser) {
        showSection('home');
        initApp();
    } else {
        showLogin();
    }
    
    setupEventListeners();
});

function setupEventListeners() {
    window.addEventListener('scroll', () => {
        const nav = document.querySelector('.navbar');
        if (window.scrollY > 50) {
            nav.classList.add('scrolled');
        } else {
            nav.classList.remove('scrolled');
        }
    });

    loginForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;
        login(email, password);
    });

    if (registerForm) {
        registerForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const email = document.getElementById('reg-email').value;
            const password = document.getElementById('reg-password').value;
            register(email, password);
        });
    }

    syncBtn.addEventListener('click', syncMovie);
    closeModal.onclick = () => modal.style.display = 'none';
    window.onclick = (e) => { if (e.target == modal) modal.style.display = 'none'; };

    // Search local
    searchInput.addEventListener('input', (e) => {
        const term = e.target.value.toLowerCase();
        if (!term) {
            refreshUI();
            return;
        }
        const filtered = allMovies.filter(m => m.title.toLowerCase().includes(term));
        renderMovies(filtered, movieGrid);
    });
}

function toggleMobileMenu() {
    const navLinks = document.querySelector('.nav-links');
    navLinks.classList.toggle('mobile-active');
}

// --- Auth & Profiles ---

async function showLogin() {
    loginSection.classList.add('active');
    profileSection.classList.remove('active');
    appContent.style.display = 'none';
    document.getElementById('ai-bot').style.display = 'none';
}

async function showProfiles() {
    loginSection.classList.remove('active');
    profileSection.classList.add('active');
    appContent.style.display = 'none';
    isManagingProfiles = false;
    document.getElementById('ai-bot').style.display = 'none';
    document.getElementById('btn-manage').textContent = 'MENAXHO PROFILET';
    document.getElementById('btn-manage').style.background = 'transparent';
    document.getElementById('btn-manage').style.color = '#808080';
    
    renderProfiles();
}

function renderProfiles() {
    profileList.className = `profile-list ${isManagingProfiles ? 'managing' : ''}`;
    profileList.innerHTML = profiles.map(p => `
        <div class="profile-card">
            <div class="profile-actions">
                <i class="fas fa-edit" title="Edito" onclick="editProfile(event, ${p.id})"></i>
                <i class="fas fa-trash" title="Fshij" onclick="deleteProfile(event, ${p.id})"></i>
            </div>
            <img src="${p.avatarUrl || 'https://upload.wikimedia.org/wikipedia/commons/0/0b/Netflix-avatar.png'}" class="profile-avatar" onclick="selectProfile(${p.id})">
            <span onclick="selectProfile(${p.id})">${p.name}</span>
        </div>
    `).join('') + `
        <div class="profile-card" onclick="createNewProfile()">
            <div class="profile-avatar" style="background: #333; display: flex; align-items: center; justify-content: center; font-size: 2rem;">
                <i class="fas fa-plus"></i>
            </div>
            <span>Shto Profil</span>
        </div>
    `;
}

function toggleManageProfiles() {
    isManagingProfiles = !isManagingProfiles;
    const btn = document.getElementById('btn-manage');
    if (isManagingProfiles) {
        btn.textContent = 'GATI';
        btn.style.background = '#fff';
        btn.style.color = '#000';
    } else {
        btn.textContent = 'MENAXHO PROFILET';
        btn.style.background = 'transparent';
        btn.style.color = '#808080';
    }
    renderProfiles();
}

async function editProfile(e, profileId) {
    e.stopPropagation();
    const currentProfile = profiles.find(p => p.id === profileId);
    if (!currentProfile) return;

    const newName = prompt("Ndrysho emrin e profilit:", currentProfile.name);
    if (!newName) return;

    try {
        const response = await fetch(`${API_BASE}/auth/${currentAccountId}/profiles/${profileId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name: newName, avatarUrl: currentProfile.avatarUrl })
        });
        
        if (response.ok) {
            const updatedProfile = await response.json();
            const index = profiles.findIndex(p => p.id === profileId);
            profiles[index] = updatedProfile;
            renderProfiles();
        } else {
            alert("Nuk u arrit modifikimi i profilit.");
        }
    } catch(err) {
        alert("Gabim gjatë modifikimit.");
    }
}

async function deleteProfile(e, profileId) {
    e.stopPropagation();
    const conf = confirm("A jeni i sigurt që doni ta fshini këtë profil?");
    if (!conf) return;

    try {
        const response = await fetch(`${API_BASE}/auth/${currentAccountId}/profiles/${profileId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            profiles = profiles.filter(p => p.id !== profileId);
            renderProfiles();
        } else {
            alert("Nuk u arrit fshirja e profilit.");
        }
    } catch(err) {
        alert("Gabim gjatë fshirjes.");
    }
}

async function createNewProfile() {
    const name = prompt("Shkruani emrin e profilit të ri:");
    if (!name) return;

    try {
        const response = await fetch(`${API_BASE}/auth/${currentAccountId}/profiles`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, avatarUrl: 'https://upload.wikimedia.org/wikipedia/commons/0/0b/Netflix-avatar.png' })
        });
        
        if (response.ok) {
            const newProfile = await response.json();
            profiles.push(newProfile);
            renderProfiles();
        } else {
            alert("Nuk u arrit krijimi i profilit.");
        }
    } catch(e) {
        alert("Gabim në lidhje.");
    }
}

function toggleAuthMode() {
    if (loginForm.style.display === 'none') {
        loginForm.style.display = 'block';
        registerForm.style.display = 'none';
        document.querySelector('.auth-card h1').textContent = 'Kyçu';
    } else {
        loginForm.style.display = 'none';
        registerForm.style.display = 'block';
        document.querySelector('.auth-card h1').textContent = 'Regjistrohu';
    }
}

// We need to keep track of the current Account ID
let currentAccountId = null;

async function login(email, password) {
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        
        if (response.ok) {
            const account = await response.json();
            currentAccountId = account.id;
            profiles = account.profiles;
            showProfiles();
        } else {
            const err = await response.json().catch(()=>({}));
            alert(`Gabim gjatë kyçjes: ${err.message || "Të dhëna të pasakta!"}`);
        }
    } catch (e) {
        alert("Gabim gjatë kyçjes.");
    }
}

async function register(email, password) {
    try {
        // We pass name="" because backend might require it, but we removed name logic.
        // Wait, UserRequest requires name. Let's send email as default name.
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name: email, email, password })
        });
        
        if (response.ok) {
            alert("Llogaria u krijua me sukses! Tani mund të kyçeni.");
            toggleAuthMode();
            document.getElementById('login-email').value = email;
            document.getElementById('login-password').value = password;
        } else {
            const err = await response.json().catch(()=>({}));
            alert(`Gabim gjatë regjistrimit: ${err.message || "Email ekziston ose të dhënat janë gabim!"}`);
        }
    } catch (e) {
        alert("Gabim gjatë regjistrimit.");
    }
}

function selectProfile(userId) {
    if (isManagingProfiles) {
        // If we are managing profiles, clicking the image should trigger edit.
        editProfile({ stopPropagation: () => {} }, userId);
        return;
    }

    const user = profiles.find(p => p.id === userId);
    currentUser = user;
    localStorage.setItem('uniflix_user', JSON.stringify(user));
    
    profileSection.classList.remove('active');
    appContent.style.display = 'block';
    document.getElementById('ai-bot').style.display = 'block';
    
    initApp();
}

function logout() {
    currentUser = null;
    localStorage.removeItem('uniflix_user');
    showLogin();
}

// --- App Logic ---

async function initApp() {
    if (!currentUser) return;
    
    document.getElementById('current-user-img').src = currentUser.avatarUrl;
    document.getElementById('ai-bot').style.display = 'block';
    
    await loadData();
    refreshUI();
}

async function loadData() {
    try {
        const [moviesRes, favRes] = await Promise.all([
            fetch(`${API_BASE}/movies`),
            fetch(`${API_BASE}/users/${currentUser.id}/favorites`)
        ]);
        
        allMovies = await moviesRes.json();
        favorites = await favRes.json();
    } catch (e) {
        console.error("Data load failed", e);
    }
}

function refreshUI() {
    // Trending: Newest 10
    const trending = [...allMovies].reverse().slice(0, 10);
    renderMovies(trending, movieGrid);
    
    // Favorites
    renderMovies(favorites, homeFavGrid);
    renderMovies(favorites, favGrid);
    
    // AI Recommendations (Static for now, updated via AI Bot)
    renderMovies(allMovies.slice(0, 5), homeRecGrid);
}

function renderMovies(list, container) {
    if (!container) return;
    if (list.length === 0) {
        container.innerHTML = '<p style="padding: 20px; color: #666;">Nuk ka të dhëna.</p>';
        return;
    }

    container.innerHTML = list.map(m => `
        <div class="movie-card" onclick="openModal(${m.id})">
            <img src="${m.imageUrl || 'https://via.placeholder.com/300x150?text=No+Image'}" alt="${m.title}">
            <div class="card-title">${m.title}</div>
        </div>
    `).join('');
}

function showSection(sectionId) {
    // Close mobile menu if open
    const navLinks = document.querySelector('.nav-links');
    navLinks.classList.remove('mobile-active');

    if (sectionId === 'profiles') {
        showProfiles();
        return;
    }

    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.nav-links a').forEach(a => a.classList.remove('active'));
    
    const section = document.getElementById(`${sectionId}-section`);
    if (section) section.classList.add('active');
    
    const hero = document.getElementById('hero-banner');
    if (sectionId === 'home') {
        hero.style.display = 'flex';
        document.getElementById('grid-title').textContent = 'Trendet e Fundit';
        document.getElementById('home-favorites-row').style.display = 'block';
        document.getElementById('home-recommendations-row').style.display = 'block';
    } else {
        hero.style.display = 'none';
    }

    // Nav active class
    const links = document.querySelectorAll('.nav-links a');
    links.forEach(a => {
        if (a.getAttribute('onclick') && a.getAttribute('onclick').includes(sectionId)) {
            a.classList.add('active');
        }
    });
}

async function filterByType(type) {
    // Close mobile menu
    document.querySelector('.nav-links').classList.remove('mobile-active');

    showSection('home');
    document.getElementById('hero-banner').style.display = 'none';
    document.getElementById('home-favorites-row').style.display = 'none';
    document.getElementById('home-recommendations-row').style.display = 'none';
    
    document.getElementById('grid-title').textContent = type === 'movie' ? 'Filma' : 'Seriale';
    
    try {
        const response = await fetch(`${API_BASE}/movies/type/${type}`);
        const filtered = await response.json();
        renderMovies(filtered, movieGrid);
    } catch (e) {
        renderMovies(allMovies.filter(m => m.type === type), movieGrid);
    }
}

// --- Sync & Modal ---

async function syncMovie() {
    let title = searchInput.value.trim();
    
    if (!title) {
        title = prompt("Shkruani titullin e filmit/serialit për të sinkronizuar:");
    }
    
    if (!title) return;
    
    const originalContent = syncBtn.innerHTML;
    syncBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    syncBtn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/movies/sync?title=${encodeURIComponent(title)}`, { method: 'POST' });
        if (response.ok) {
            await loadData();
            refreshUI();
            searchInput.value = '';
            alert(`"${title}" u shtua me sukses!`);
        } else {
            const err = await response.json().catch(() => ({}));
            alert(`Dështoi sinkronizimi: ${err.message || "Titulli nuk u gjet."}`);
        }
    } catch (e) {
        alert("Gabim në lidhjen me serverin.");
    } finally {
        syncBtn.innerHTML = originalContent;
        syncBtn.disabled = false;
    }
}

async function openModal(id) {
    try {
        const res = await fetch(`${API_BASE}/movies/${id}`);
        const movie = await res.json();
        
        document.getElementById('modal-title').textContent = movie.title;
        document.getElementById('modal-desc').textContent = movie.description || "Nuk ka përshkrim.";
        document.getElementById('modal-year').textContent = movie.releaseYear;
        document.getElementById('modal-genre').textContent = movie.genre;
        document.getElementById('modal-banner').style.backgroundImage = `url(${movie.imageUrl})`;
        
        document.getElementById('modal-play-btn').onclick = () => watchTrailer(movie.title);
        
        updateFavoriteButton(movie.id);
        setupStars(movie.id);
        
        modal.style.display = 'flex';
    } catch (e) {
        console.error("Modal failed", e);
    }
}

async function updateFavoriteButton(movieId) {
    const btn = document.getElementById('modal-fav-btn');
    const isFav = favorites.some(f => f.id === movieId);
    
    btn.innerHTML = isFav ? '<i class="fas fa-check"></i>' : '<i class="fas fa-plus"></i>';
    btn.onclick = () => toggleFavorite(movieId);
}

async function toggleFavorite(movieId) {
    const isFav = favorites.some(f => f.id === movieId);
    const method = isFav ? 'DELETE' : 'POST';
    
    try {
        await fetch(`${API_BASE}/users/${currentUser.id}/favorites?movieId=${movieId}`, { method });
        await loadData();
        refreshUI();
        updateFavoriteButton(movieId);
    } catch (e) {
        console.error("Favorite failed", e);
    }
}

function setupStars(movieId) {
    document.querySelectorAll('.stars i').forEach(star => {
        star.onclick = async () => {
            const score = star.dataset.value;
            await fetch(`${API_BASE}/ratings`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ userId: currentUser.id, movieId, score })
            });
            alert("Vlerësimi u ruajt!");
        };
    });
}

// --- AI Bot ---

function toggleAiBot() {
    const container = document.getElementById('ai-bot');
    container.classList.toggle('chat-active');
    
    const win = document.getElementById('ai-chat-window');
    if (container.classList.contains('chat-active')) {
        win.style.display = 'flex';
    } else {
        win.style.display = 'none';
    }
}

async function sendAiMessage() {
    const input = document.getElementById('ai-input');
    const text = input.value.trim();
    if (!text) return;
    
    addMessage(text, 'user');
    input.value = '';
    
    try {
        const response = await fetch(`${API_BASE}/ai/chat`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: text })
        });
        const data = await response.json();
        
        addMessage(data.response, 'ai');
        if (data.movies && data.movies.length > 0) {
            addMovieRecommendation(data.movies);
        }
    } catch (e) {
        addMessage("Më vjen keq, nuk munda t'ju përgjigjem.", 'ai');
    }
}

function addMessage(text, side) {
    const container = document.getElementById('ai-chat-messages');
    const div = document.createElement('div');
    div.className = `message ${side}`;
    div.textContent = text;
    container.appendChild(div);
    container.scrollTop = container.scrollHeight;
}

function addMovieRecommendation(movies) {
    const container = document.getElementById('ai-chat-messages');
    const div = document.createElement('div');
    div.className = 'ai-movie-list';
    div.innerHTML = movies.map(m => `
        <img src="${m.imageUrl}" title="${m.title}" class="ai-movie-item" onclick="openModal(${m.id})">
    `).join('');
    container.appendChild(div);
    container.scrollTop = container.scrollHeight;
}

function showHowItWorks() {
    document.getElementById('how-it-works-modal').style.display = 'flex';
}

function watchTrailer(title) {
    const query = encodeURIComponent(title + " official trailer");
    window.open(`https://www.youtube.com/results?search_query=${query}`, '_blank');
}

function scrollToTrending() {
    const element = document.getElementById('trending-section');
    if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
    }
}

function closeHowItWorks() {
    document.getElementById('how-it-works-modal').style.display = 'none';
}

window.addEventListener('click', (e) => {
    const infoModal = document.getElementById('how-it-works-modal');
    if (e.target == infoModal) {
        infoModal.style.display = 'none';
    }
});
