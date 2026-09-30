const topGazdmuts = [
        {
            "aaAzon": 2497,
            "vallalatMeret": "kis",
            "teaorKategoria": "tobbi szolgaltatas",
            "totalExpense": 131590599
        },
        {
            "aaAzon": 1538,
            "vallalatMeret": "kozep 100 felett",
            "teaorKategoria": "kereskedelem + szallitas",
            "totalExpense": 100732984
        },
        {
            "aaAzon": 7077,
            "vallalatMeret": "kozep 100 alatt",
            "teaorKategoria": "kereskedelem + szallitas",
            "totalExpense": 69116310
        },
        {
            "aaAzon": 9454,
            "vallalatMeret": "kozep 100 alatt",
            "teaorKategoria": "epitoipar",
            "totalExpense": 67999306
        },
        {
            "aaAzon": 5681,
            "vallalatMeret": "kozep 100 felett",
            "teaorKategoria": "tobbi szolgaltatas",
            "totalExpense": 65422848
        },
        {
            "aaAzon": 6381,
            "vallalatMeret": "kozep 100 felett",
            "teaorKategoria": "tobbi ipar",
            "totalExpense": 58671049
        },
        {
            "aaAzon": 1903,
            "vallalatMeret": "kozep 100 felett",
            "teaorKategoria": "epitoipar",
            "totalExpense": 51642648
        },
        {
            "aaAzon": 8121,
            "vallalatMeret": "nagy",
            "teaorKategoria": "tobbi ipar",
            "totalExpense": 48041035
        },
        {
            "aaAzon": 8778,
            "vallalatMeret": "nagy",
            "teaorKategoria": "kereskedelem + szallitas",
            "totalExpense": 42457216
        },
        {
            "aaAzon": 2905,
            "vallalatMeret": "kozep 100 felett",
            "teaorKategoria": "tobbi ipar",
            "totalExpense": 41386072
        }
    ];

const topPurchases = [
    {
        "esstId": 50830,
        "xBruttoHuf": 34567086,
        "category": [
            "konyvel"
        ],
        "customer": {
            "aaAzon": 9454,
            "teaorKategoria": "epitoipar",
            "vallalatMeret": "kozep 100 alatt"
        }
    },
    {
        "esstId": 29878,
        "xBruttoHuf": 9687567,
        "category": [
            "konyvel"
        ],
        "customer": {
            "aaAzon": 3209,
            "teaorKategoria": "epitoipar",
            "vallalatMeret": "mikro"
        }
    },
    {
        "esstId": 62375,
        "xBruttoHuf": 9672045,
        "category": [
            "konyvel",
            "egyeb"
        ],
        "customer": {
            "aaAzon": 9454,
            "teaorKategoria": "epitoipar",
            "vallalatMeret": "kozep 100 alatt"
        }
    },
    {
        "esstId": 74006,
        "xBruttoHuf": 8147558,
        "category": [
            "konyvel"
        ],
        "customer": {
            "aaAzon": 2497,
            "teaorKategoria": "tobbi szolgaltatas",
            "vallalatMeret": "kis"
        }
    },
    {
        "esstId": 76921,
        "xBruttoHuf": 7701756,
        "category": [
            "konyvel"
        ],
        "customer": {
            "aaAzon": 5681,
            "teaorKategoria": "tobbi szolgaltatas",
            "vallalatMeret": "kozep 100 felett"
        }
    },
    {
        "esstId": 71095,
        "xBruttoHuf": 7514014,
        "category": [
            "konyvel"
        ],
        "customer": {
            "aaAzon": 6381,
            "teaorKategoria": "tobbi ipar",
            "vallalatMeret": "kozep 100 felett"
        }
    },
    {
        "esstId": 36173,
        "xBruttoHuf": 7464529,
        "category": [
            "konyvel"
        ],
        "customer": {
            "aaAzon": 8778,
            "teaorKategoria": "kereskedelem + szallitas",
            "vallalatMeret": "nagy"
        }
    },
    {
        "esstId": 74279,
        "xBruttoHuf": 7153749,
        "category": [
            "konyvel"
        ],
        "customer": {
            "aaAzon": 6381,
            "teaorKategoria": "tobbi ipar",
            "vallalatMeret": "kozep 100 felett"
        }
    },
    {
        "esstId": 69450,
        "xBruttoHuf": 7005379,
        "category": [
            "konyvel",
            "egyeb"
        ],
        "customer": {
            "aaAzon": 2497,
            "teaorKategoria": "tobbi szolgaltatas",
            "vallalatMeret": "kis"
        }
    },
    {
        "esstId": 55226,
        "xBruttoHuf": 6830379,
        "category": [
            "konyvel"
        ],
        "customer": {
            "aaAzon": 6595,
            "teaorKategoria": "tobbi szolgaltatas",
            "vallalatMeret": "nagy"
        }
    }
];

const taeorCategories = [ 
    "kereskedelem + szallitas",
    "szallashely + vendeglatas",
	"tobbi szolgaltatas",
	"mezogazdasag + elelmiszeripar",
    "energiaszektor + banya",
    "tobbi ipar",
];

const sizes = [
    "mikro",
    "kis",
    "kozep 100 alatt",
    "kozep 100 felett",
    "nagy"
];

const invoiceCategories = [
    "konyvel",
	"n_szoft",
    "alt_szoft",
	"vam",
	"szek",
	"jovedek",
    "egyeb",
]

const taskOpen = document.querySelector("#task-ul");
const analyzeOpen = document.querySelector("#analyze-ul");
const resultOpen = document.querySelector("#result-ul");

const taskSection = document.querySelector("#task-section");
const analyzeSection = document.querySelector("#analyze-section");
const resultSection = document.querySelector("#result-section");

const taeorListBody = document.querySelector("#taeor-enum");
const sizesListBody = document.querySelector("#sizes-enum");
const invoiceCategoriesListBody = document.querySelector("#invoice-category-enum");

const topGazdmutsBody = document.querySelector("#topGazdmuts-list");
const topPurchasesBody = document.querySelector("#topPurchases-list");

function hideAllSections()
{
    taskSection.className = "no-container";
    analyzeSection.className = "no-container";
    resultSection.className = "no-container";
}

function openTaskSection()
{
    hideAllSections();
    taskSection.className = "section-container";
}

function openAnalyzeSection()
{
    hideAllSections();
    analyzeSection.className = "section-container";
}

function openResultSection()
{
    hideAllSections();
    resultSection.className = "section-container";
}

function setActive(element)
{
    document.querySelectorAll(".menu-point a")
	  .forEach(el => el.classList.remove("active"));

    element.classList.add("active");
}

function jumpTo(targetId)
{
    const target = document.querySelector("#" + targetId);

    if(target)
    {
        target.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }
	
}

function formatCurrency(value)
{
    return value.toLocaleString('hu-HU') + ' Ft';
}

function displayCategoriesList(arr, element){
	element.innerHTML = arr.map((e) => {
		return `<li><span class="category-item">${e}</span></li>`
	}).join('');
}

function displayTopGazdmuts()
{
    topGazdmutsBody.innerHTML = topGazdmuts.map((gazdmut) => {
        const { aaAzon, vallalatMeret, teaorKategoria, totalExpense } = gazdmut;

        return `
            <tr>
                <td>${aaAzon}</td>
                <td>${vallalatMeret}</td>
                <td>${teaorKategoria}</td>
                <td>${formatCurrency(totalExpense)}</td>
            </tr>
        `;
    }).join('');
}

function displayTopPurchases(){
	
	topPurchasesBody.innerHTML = topPurchases.map((purchase) => {
		const { esstId, xBruttoHuf, category, customer } = purchase;

        const categoryList = `
            <ul class="inner-list">
                ${category.map(item => `<li>${item}</li>`).join('')}
            </ul>
        `;

        return `
            <tr>
                <td>${esstId}</td>
                <td>${formatCurrency(xBruttoHuf)}</td>
                <td>${categoryList}</td>
                <td>${customer.aaAzon}</td>
                <td>${customer.teaorKategoria}</td>
                <td>${customer.vallalatMeret}</td>
            </tr>
        `;
	}).join('');
}

taskOpen.addEventListener("click", function(){openTaskSection(); setActive(this.querySelector("a"));});
analyzeOpen.addEventListener("click", function(){openAnalyzeSection(); setActive(this.querySelector("a"));});
resultOpen.addEventListener("click", function(){openResultSection(); setActive(this.querySelector("a"));});

displayCategoriesList(taeorCategories, taeorListBody);
displayCategoriesList(sizes, sizesListBody);
displayCategoriesList(invoiceCategories, invoiceCategoriesListBody);

displayTopGazdmuts();
displayTopPurchases();	