const destinationNames = ['Armenia', 'Calarcá', 'Salento', 'Montenegro', 'Quimbaya', 'Circasia'];
const weights = [4.5, 12.0, 3.2, 18.7, 7.4, 5.1, 2.8, 9.3, 6.4, 11.2];
const priorities = [3, 5, 2, 4, 5, 1, 3, 4, 2, 5];
const times = [30, 45, 25, 20, 18, 55, 32, 27, 40, 22];
const defaultPackages = Array.from({length: 30}, (_, i) => ({
    code: `PQ${105 + i * 37}`, destination: destinationNames[i % destinationNames.length],
    weight: weights[i % weights.length], priority: priorities[i % priorities.length],
    time: times[i % times.length], status: 'Pendiente', arrival: i, courierId: null
}));
const defaultCouriers = [
    {id: 101, name: 'Laura Gómez', zone: 'Armenia', available: true},
    {id: 102, name: 'Carlos Ramírez', zone: 'Calarcá', available: true},
    {id: 103, name: 'Diana López', zone: 'Salento', available: true},
    {id: 104, name: 'Andrés Marín', zone: 'Montenegro', available: true},
    {id: 105, name: 'Sofía Torres', zone: 'Quimbaya', available: true},
    {id: 106, name: 'Mateo Ruiz', zone: 'Circasia', available: false}
];
const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];
const esc = (value) => String(value).replace(/[&<>"']/g, (char) => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[char]));
const state = loadState();
let packages = state?.packages ?? structuredClone(defaultPackages);
let couriers = state?.couriers ?? structuredClone(defaultCouriers);
let activities = state?.activities ?? [];
let orderMode = 'prioridad';
let packageSortPriority = false;
let toastTimer;
let currentView = 'inicio';
let packagePage = 0;
const pageSize = 6;
const pageTitles = {inicio:'Resumen', paquetes:'Paquetes', repartidores:'Repartidores', destinos:'Destinos', historial:'Historial'};

function loadState() {
    try { return JSON.parse(localStorage.getItem('quindioexpress-web-state')); }
    catch { return null; }
}
function saveState() {
    try { localStorage.setItem('quindioexpress-web-state', JSON.stringify({packages, couriers, activities})); }
    catch { /* La interfaz sigue funcionando aunque el navegador bloquee el almacenamiento local. */ }
}
function notify(message) {
    const toast = $('#toast');
    toast.textContent = message;
    toast.classList.add('show');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => toast.classList.remove('show'), 2800);
}
function addActivity(message, type = 'green') {
    activities.unshift({message, type, time: new Date().toLocaleTimeString('es-CO', {hour:'2-digit', minute:'2-digit'})});
    activities = activities.slice(0, 20);
}
function statusClass(status) {
    return status === 'Pendiente' ? 'status-pending' : status === 'En camino' ? 'status-transit' : 'status-delivered';
}
function priorityDots(value) {
    return `<span class="priority-dots" aria-label="Prioridad ${value}">${[1,2,3,4,5].map((n) => `<i class="${n <= value ? 'on' : ''}"></i>`).join('')}</span><span>${value}</span>`;
}
function filteredPackages(query = '', filter = 'todos', sorted = false) {
    let result = packages.filter((pack) => (filter === 'todos' || pack.status === filter) &&
        `${pack.code} ${pack.destination}`.toLocaleLowerCase('es').includes(query.trim().toLocaleLowerCase('es')));
    if (sorted) result.sort((a,b) => b.priority - a.priority || a.time - b.time || a.code.localeCompare(b.code));
    return result;
}
function packageRows(list) {
    return list.map((pack) => `<tr>
    <td>${esc(pack.code)}</td><td class="destination-cell">${esc(pack.destination)}</td>
    <td class="weight-cell">${Number(pack.weight).toFixed(1)} kg</td><td><span class="priority">${priorityDots(pack.priority)}</span></td>
    <td><span class="status-pill ${statusClass(pack.status)}">${esc(pack.status)}</span></td>
    <td>${pack.status === 'En camino' ? `<button class="row-menu deliver-button" data-code="${esc(pack.code)}" title="Registrar entrega" aria-label="Registrar entrega de ${esc(pack.code)}">✓</button>` : '<span class="muted-dash">—</span>'}</td></tr>`).join('');
}
function attachDeliveryButtons(root = document) {
    $$('.deliver-button', root).forEach((button) => button.addEventListener('click', () => deliverPackage(button.dataset.code)));
}
function renderStats() {
    const counts = Object.fromEntries(['Pendiente','En camino','Entregado'].map((status) => [status, packages.filter((pack) => pack.status === status).length]));
    $('#stat-total').textContent = packages.length;
    $('#stat-pending').textContent = counts['Pendiente'];
    $('#stat-transit').textContent = counts['En camino'];
    $('#stat-delivered').textContent = counts['Entregado'];
    $('#nav-package-count').textContent = packages.length;
    $('#pending-bar').style.width = `${packages.length ? counts['Pendiente'] / packages.length * 100 : 0}%`;
    $('#transit-bar').style.width = `${packages.length ? counts['En camino'] / packages.length * 100 : 0}%`;
    $('#delivered-bar').style.width = `${packages.length ? counts['Entregado'] / packages.length * 100 : 0}%`;
}
function renderDashboardPackages() {
    const list = filteredPackages($('#search-input').value, $('#status-filter').value, packageSortPriority);
    const pages = Math.max(1, Math.ceil(list.length / pageSize));
    packagePage = Math.min(packagePage, pages - 1);
    const visible = list.slice(packagePage * pageSize, (packagePage + 1) * pageSize);
    $('#package-rows').innerHTML = packageRows(visible);
    $('#empty-state').hidden = list.length > 0;
    $('#table-summary').textContent = `Mostrando ${visible.length ? packagePage * pageSize + 1 : 0}–${packagePage * pageSize + visible.length} de ${list.length} paquete${list.length === 1 ? '' : 's'}`;
    $('.pagination').innerHTML = `<button id="previous-page" aria-label="Página anterior" ${packagePage === 0 ? 'disabled' : ''}>‹</button><button class="current-page">${packagePage + 1} / ${pages}</button><button id="next-page" aria-label="Página siguiente" ${packagePage >= pages - 1 ? 'disabled' : ''}>›</button>`;
    $('#previous-page').addEventListener('click', () => { packagePage--; renderDashboardPackages(); });
    $('#next-page').addEventListener('click', () => { packagePage++; renderDashboardPackages(); });
    attachDeliveryButtons($('#package-rows'));
}
function orderedPending() {
    const pending = packages.filter((pack) => pack.status === 'Pendiente');
    if (orderMode === 'llegada') return pending.sort((a,b) => a.arrival - b.arrival);
    return pending.sort((a,b) => b.priority - a.priority || a.time - b.time || a.code.localeCompare(b.code));
}
function renderDispatch() {
    const next = orderedPending()[0];
    const box = $('#next-package');
    const button = $('#dispatch-button');
    if (!next) {
        box.innerHTML = '<div class="package-symbol">✓</div><div class="next-meta"><strong>Todo al día</strong><span>No hay paquetes pendientes de despacho.</span></div>';
        button.disabled = true;
        $('#dispatch-hint').textContent = 'Registra un paquete nuevo para continuar.';
        return;
    }
    const available = couriers.some((person) => person.available && person.zone.toLocaleLowerCase('es') === next.destination.toLocaleLowerCase('es'));
    box.innerHTML = `<div class="package-symbol">▣</div><div class="next-meta"><strong>${esc(next.code)}</strong><span>${esc(next.destination)} · ${Number(next.weight).toFixed(1)} kg</span></div><span class="next-priority">Prioridad ${next.priority}</span>`;
    button.disabled = !available;
    $('#dispatch-hint').textContent = available ? (orderMode === 'prioridad' ? 'Siguiente según prioridad, tiempo y código.' : 'Se respeta el orden de llegada.') : `No hay repartidor disponible en ${next.destination}.`;
}
function courierMarkup(courier) {
    const assigned = packages.filter((pack) => pack.courierId === courier.id);
    const active = assigned.filter((pack) => pack.status === 'En camino');
    const initials = courier.name.split(' ').slice(0,2).map((part) => part[0]).join('');
    return `<article class="panel courier-detail-card"><div class="courier-card-head"><div class="courier-avatar large-avatar">${esc(initials)}</div><div class="courier-info"><strong>${esc(courier.name)}</strong><span>Identificación ${courier.id} · Zona ${esc(courier.zone)}</span></div><span class="availability ${courier.available ? '' : 'busy'}">${courier.available ? 'Disponible' : 'Ocupado'}</span></div>
    <div class="courier-metrics"><div><strong>${assigned.length}</strong><span>Pedidos asignados</span></div><div><strong>${active.length}</strong><span>En camino</span></div><div><strong>${assigned.filter((pack) => pack.status === 'Entregado').length}</strong><span>Entregados</span></div></div>
    <div class="assigned-orders"><div class="assigned-title">Pedidos de este repartidor <span>${assigned.length}</span></div>
    ${assigned.length ? assigned.map((pack) => `<div class="assigned-order"><div><strong>${esc(pack.code)}</strong><span>${esc(pack.destination)} · ${Number(pack.weight).toFixed(1)} kg · Prioridad ${pack.priority}</span></div><span class="status-pill ${statusClass(pack.status)}">${esc(pack.status)}</span>${pack.status === 'En camino' ? `<button class="small-action deliver-button" data-code="${esc(pack.code)}">Marcar entregado</button>` : ''}</div>`).join('') : '<p class="no-assigned">Aún no tiene pedidos asignados.</p>'}
    </div></article>`;
}
function renderCourierPreview() {
    $('#courier-list').innerHTML = couriers.slice(0, 5).map((courier) => {
        const initials = courier.name.split(' ').slice(0,2).map((part) => part[0]).join('');
        const currentCount = packages.filter((pack) => pack.courierId === courier.id && pack.status === 'En camino').length;
        return `<div class="courier"><div class="courier-avatar">${esc(initials)}</div><div class="courier-info"><strong>${esc(courier.name)}</strong><span>${esc(courier.zone)} · ${currentCount} pedido${currentCount === 1 ? '' : 's'} en camino</span></div><span class="availability ${courier.available ? '' : 'busy'}">${courier.available ? 'Disponible' : 'Ocupado'}</span></div>`;
    }).join('');
}
function renderDestinationsPreview() {
    const counts = destinationNames.map((name) => ({name, count: packages.filter((pack) => pack.destination.toLocaleLowerCase('es') === name.toLocaleLowerCase('es') && pack.status === 'Pendiente').length}));
    const maximum = Math.max(1, ...counts.map((item) => item.count));
    $('#destination-list').innerHTML = counts.map((item) => `<div class="destination-item"><div class="destination-label"><span>${esc(item.name)}</span><strong>${item.count}</strong></div><div class="destination-bar"><i style="width:${item.count / maximum * 100}%"></i></div></div>`).join('');
}
function activityMarkup(item) {
    return `<div class="activity"><div class="activity-icon ${item.type === 'blue' ? 'blue-activity' : ''}">${item.type === 'blue' ? '➤' : '✓'}</div><div class="activity-copy">${item.message}</div><time>${esc(item.time)}</time></div>`;
}
function renderActivityPreview() {
    $('#activity-list').innerHTML = activities.length ? activities.slice(0,3).map(activityMarkup).join('') : '<div class="activity"><div class="activity-icon">✓</div><div class="activity-copy"><strong>Sistema listo</strong> · 30 paquetes cargados</div><time>Ahora</time></div><div class="activity"><div class="activity-icon blue-activity">⌖</div><div class="activity-copy"><strong>6 destinos</strong> disponibles en el Quindío</div><time>Hoy</time></div>';
}
function renderDashboard() {
    renderStats(); renderDashboardPackages(); renderDispatch(); renderCourierPreview(); renderDestinationsPreview(); renderActivityPreview();
}
function viewHeading(kicker, title, subtitle, action = '') {
    return `<section class="page-heading secondary-heading"><div><p class="eyebrow">${kicker}</p><h1>${title}</h1><p class="subtitle">${subtitle}</p></div>${action}</section>`;
}
function packagesPage() {
    const action = '<button class="primary-button" data-open-register><span>＋</span> Registrar paquete</button>';
    return `${viewHeading('GESTIÓN DE ENVÍOS', 'Todos los paquetes', 'Busca, filtra y revisa el estado de cada envío.', action)}
    <section class="panel full-page-panel"><div class="toolbar"><label class="search-box"><span>⌕</span><input id="all-search" type="search" placeholder="Buscar por código o destino..."></label><select id="all-status"><option value="todos">Todos los estados</option><option>Pendiente</option><option>En camino</option><option>Entregado</option></select><button class="filter-button" id="all-priority-sort">☷ <span>Ordenar por prioridad</span></button></div>
    <div class="table-wrap"><table><thead><tr><th>CÓDIGO</th><th>DESTINO</th><th>PESO</th><th>PRIORIDAD</th><th>ESTADO</th><th>ACCIÓN</th></tr></thead><tbody id="all-package-rows"></tbody></table><div class="empty-state" id="all-empty" hidden>No hay paquetes que coincidan con la búsqueda.</div></div><div class="page-table-summary" id="all-summary"></div></section>`;
}
function renderAllPackages() {
    if (!$('#all-package-rows')) return;
    const list = filteredPackages($('#all-search').value, $('#all-status').value, $('#all-priority-sort').dataset.sorted === 'true');
    $('#all-package-rows').innerHTML = packageRows(list);
    $('#all-empty').hidden = list.length > 0;
    $('#all-summary').textContent = `${list.length} paquete${list.length === 1 ? '' : 's'} · ${list.filter((pack) => pack.status === 'Pendiente').length} pendientes · ${list.filter((pack) => pack.status === 'En camino').length} en camino · ${list.filter((pack) => pack.status === 'Entregado').length} entregados`;
    attachDeliveryButtons($('#all-package-rows'));
}
function couriersPage() {
    const assigned = packages.filter((pack) => pack.status === 'En camino').length;
    const available = couriers.filter((courier) => courier.available).length;
    return `${viewHeading('EQUIPO DE ENTREGA', 'Repartidores', 'Disponibilidad y pedidos asignados a cada persona.')}
    <section class="courier-summary-grid"><article class="stat-card"><div class="stat-top"><span>Repartidores registrados</span><span class="stat-icon lilac">♙</span></div><div class="stat-value">${couriers.length}</div><div class="stat-note">Equipo total</div></article><article class="stat-card"><div class="stat-top"><span>Disponibles</span><span class="stat-icon green">✓</span></div><div class="stat-value">${available}</div><div class="stat-note">Listos para recibir un pedido</div></article><article class="stat-card"><div class="stat-top"><span>Pedidos en camino</span><span class="stat-icon blue">➤</span></div><div class="stat-value">${assigned}</div><div class="stat-note">Actualmente asignados</div></article></section>
    <section class="courier-detail-grid">${couriers.map(courierMarkup).join('')}</section>`;
}
function destinationsPage() {
    const cards = destinationNames.map((name) => {
        const list = packages.filter((pack) => pack.destination.toLocaleLowerCase('es') === name.toLocaleLowerCase('es'));
        const pending = list.filter((pack) => pack.status === 'Pendiente').length;
        const transit = list.filter((pack) => pack.status === 'En camino').length;
        const delivered = list.filter((pack) => pack.status === 'Entregado').length;
        return `<article class="panel destination-card"><div class="destination-card-top"><span class="destination-pin">⌖</span><span class="muted-label">MUNICIPIO</span></div><h2>${esc(name)}</h2><p>${list.length} paquete${list.length === 1 ? '' : 's'} registrados</p><div class="destination-statuses"><span class="status-pill status-pending">${pending} pendientes</span><span class="status-pill status-transit">${transit} en camino</span><span class="status-pill status-delivered">${delivered} entregados</span></div><div class="destination-package-list">${list.slice(0,5).map((pack) => `<div><strong>${esc(pack.code)}</strong><span class="status-pill ${statusClass(pack.status)}">${esc(pack.status)}</span></div>`).join('')}${list.length > 5 ? `<small>y ${list.length - 5} más</small>` : ''}</div></article>`;
    }).join('');
    return `${viewHeading('COBERTURA', 'Destinos', 'Estado de los paquetes en cada municipio atendido.')}
    <section class="destination-detail-grid">${cards}</section>`;
}
function historyPage() {
    const content = activities.length ? activities.map(activityMarkup).join('') : '<div class="history-empty"><span>◷</span><h2>Sin movimientos todavía</h2><p>Cuando registres, despaches o entregues paquetes, aparecerán aquí.</p></div>';
    return `${viewHeading('SEGUIMIENTO', 'Historial de actividad', 'Registro de los últimos cambios realizados en la operación.')}
    <section class="panel history-panel"><div class="panel-heading"><div><h2>Movimientos recientes</h2><p>Los eventos nuevos aparecen primero.</p></div><span class="live-dot"></span></div><div class="history-list">${content}</div></section>`;
}
function openView(view, updateHash = true) {
    if (!pageTitles[view]) view = 'inicio';
    currentView = view;
    $('#dashboard-view').hidden = view !== 'inicio';
    const secondary = $('#secondary-view');
    secondary.hidden = view === 'inicio';
    if (view !== 'inicio') {
        const page = {paquetes:packagesPage, repartidores:couriersPage, destinos:destinationsPage, historial:historyPage}[view]();
        secondary.innerHTML = page;
        if (view === 'paquetes') {
            $('#all-search').addEventListener('input', renderAllPackages);
            $('#all-status').addEventListener('change', renderAllPackages);
            $('#all-priority-sort').addEventListener('click', (event) => {
                const button = event.currentTarget;
                button.dataset.sorted = button.dataset.sorted !== 'true';
                button.classList.toggle('active-filter', button.dataset.sorted === 'true');
                renderAllPackages();
            });
            renderAllPackages();
        }
        if (view === 'repartidores') attachDeliveryButtons(secondary);
    }
    $('#breadcrumb-current').textContent = pageTitles[view];
    $$('.nav-item').forEach((item) => item.classList.toggle('active', item.dataset.view === view));
    if (updateHash) history.replaceState(null, '', `#${view}`);
    window.scrollTo({top:0, behavior:'smooth'});
}
function render() {
    renderDashboard();
    if (currentView !== 'inicio') openView(currentView, false);
    saveState();
}
function dispatchNext() {
    const next = orderedPending()[0];
    if (!next) return;
    const courier = couriers.find((person) => person.available && person.zone.toLocaleLowerCase('es') === next.destination.toLocaleLowerCase('es'));
    if (!courier) { notify(`No hay repartidor disponible en ${next.destination}.`); return; }
    courier.available = false;
    next.status = 'En camino';
    next.courierId = courier.id;
    addActivity(`<strong>${esc(next.code)}</strong> asignado a ${esc(courier.name)} · ${esc(next.destination)}`, 'blue');
    render();
    notify(`${next.code} asignado a ${courier.name}.`);
}
function deliverPackage(code) {
    const pack = packages.find((item) => item.code === code);
    if (!pack || pack.status !== 'En camino') return;
    const courier = couriers.find((person) => person.id === pack.courierId);
    if (courier) courier.available = true;
    pack.status = 'Entregado';
    addActivity(`<strong>${esc(pack.code)}</strong> entregado en ${esc(pack.destination)}`);
    render();
    notify(`Entrega de ${pack.code} registrada.`);
}
function resetDemo() {
    if (!window.confirm('¿Restablecer los datos de demostración? Se borrarán los cambios guardados en este navegador.')) return;
    packages = structuredClone(defaultPackages);
    couriers = structuredClone(defaultCouriers);
    activities = [];
    localStorage.removeItem('quindioexpress-web-state');
    render();
    openView('inicio');
    notify('Datos de demostración restablecidos.');
}
function registerPackage(event) {
    event.preventDefault();
    const formElement = event.currentTarget;
    const form = new FormData(formElement);
    const code = String(form.get('codigo')).trim().toUpperCase();
    if (packages.some((pack) => pack.code === code)) { notify('Ya existe un paquete con ese código.'); return; }
    const pack = {code, destination: form.get('destino'), weight: Number(form.get('peso')), priority: Number(form.get('prioridad')), time: Number(form.get('tiempo')), status: 'Pendiente', arrival: Date.now(), courierId: null};
    if (!pack.destination || pack.weight <= 0 || pack.time <= 0) { notify('Revisa los datos del paquete.'); return; }
    packages.push(pack);
    addActivity(`<strong>${esc(code)}</strong> registrado para ${esc(pack.destination)}`);
    formElement.reset();
    $('#register-dialog').close();
    render();
    notify(`Paquete ${code} registrado.`);
}

$('#today-date').textContent = new Date().toLocaleDateString('es-CO', {day:'numeric', month:'short', year:'numeric'});
$('#search-input').addEventListener('input', () => { packagePage = 0; renderDashboardPackages(); });
$('#status-filter').addEventListener('change', () => { packagePage = 0; renderDashboardPackages(); });
$('#view-all').addEventListener('click', () => openView('paquetes'));
$('#priority-sort').addEventListener('click', (event) => {
    packageSortPriority = !packageSortPriority;
    event.currentTarget.classList.toggle('active-filter', packageSortPriority);
    packagePage = 0;
    renderDashboardPackages();
});
$('#dispatch-button').addEventListener('click', dispatchNext);
$$('.dispatch-toggle button').forEach((button) => button.addEventListener('click', () => {
    orderMode = button.dataset.order;
    $$('.dispatch-toggle button').forEach((item) => item.classList.toggle('selected', item === button));
    renderDispatch();
}));
$('#open-register').addEventListener('click', () => $('#register-dialog').showModal());
$$('.close-dialog').forEach((button) => button.addEventListener('click', () => $('#register-dialog').close()));
$('#register-form').addEventListener('submit', registerPackage);
document.addEventListener('click', (event) => {
    const routeLink = event.target.closest('[data-view]');
    if (routeLink) { event.preventDefault(); openView(routeLink.dataset.view); return; }
    if (event.target.closest('[data-open-register]')) $('#register-dialog').showModal();
});
$('#notifications-button').addEventListener('click', () => openView('historial'));
$('#help-link').addEventListener('click', (event) => { event.preventDefault(); notify('Usa el menú lateral para cambiar entre las vistas del sistema.'); });
$('#reset-demo').addEventListener('click', resetDemo);
window.addEventListener('hashchange', () => openView(location.hash.slice(1) || 'inicio', false));
render();
openView(pageTitles[location.hash.slice(1)] ? location.hash.slice(1) : 'inicio', false);
