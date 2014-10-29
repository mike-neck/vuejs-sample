$(function(){
    var navigation = new Vue({
        el: "#navigation",
        data: {
            navigation: [{
                now: true,
                href: "bootstrap.html",
                title: "Home"
            }, {
                now: false,
                href: "bootstrap.html",
                title: "About"
            }, {
                now: false,
                href: "bootstrap.html",
                title: "Contact"
            }]
        }
    });
    var ticketType = new Vue({
        el: "#type",
        data: {
            items: [
                {
                    value: "bug",
                    name: "バグ"
                }, {
                    value: "issue",
                    name: "やること"
                }, {
                    value: "require",
                    name: "要望"
                }
            ]
        }
    });
    var map = {};
    ticketType.items.forEach(function(item){
        map[item.value] = item.name;
    });
    var issues = new Vue({
        el: "#issues",
        data: {
            items: []
        },
        methods: {
            updateStatus: function(item) {
                var status = item.status;
                if (status === "created") {
                    item.status = "proceeding";
                    item.proceeded = new Date();
                } else if (status === "proceeding") {
                    item.status = "done";
                    item.done = new Date();
                }
            }
        }
    });
    var Issue = function(title, detail, type){
        var now = new Date(),
            panelType = (type === "bug")? "panel-danger": (type === "issue")? "panel-warning":"panel-info";
        return {
            title: title,
            detail: detail,
            type: type,
            panelType: panelType,
            typeName: map[type],
            created: now,
            status: "created",
            proceeded: null,
            done: null
        };
    };
    var input = new Vue({
        el: "#input",
        data: {
            title: "", detail: "", type: "--"
        },
        methods: {
            addItem: function() {
                if (title.value === "" || type.value === "--") {
                    return;
                }
                var issue = Issue(title.value, detail.value, type.value);
                issues.items.push(issue);
                title.value = "";
                detail.value = "";
                type.value = "--";
            }
        }
    });
});
