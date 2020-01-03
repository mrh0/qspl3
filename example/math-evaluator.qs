println["Input an math expression, symbols separated by spaces:"];
code = "3 + 5 * 2 - 3";
print["Input: "];
code = read[];
println["Expression: " + code];
tokens = code/" ";
vals = new [];
ops = new [];

operators = new ["+", "-", "*", "/", "%"];

opvals = func s:
	s == "+":
		exit 1;
	s == "-":
		exit 1;
	s == "*":
		exit 2;
	s == "/":
		exit 2;
	s == "%":
		exit 2;
//	exit -1;

calc = func v1, v2, op:
	op == "+":
		exit v1 + v2;
	op == "-":
		exit v1 - v2;
	op == "*":
		exit v1 * v2;
	op == "/":
		exit v1 / v2;
	op == "%":
		exit v1 % v2;
//	exit -1;

res = new [];

i = 0;
i < tokens[]::
	c = tokens[i];
	operators ? c:
		ops[] == 0:
			ops#push[c];
		else:
			opvals[ops#peek[]] < opvals[c]:
				ops#push[c];
			else:
				opvals[ops#peek[]] >= opvals[c]::
					res#push[ops#pop[]];
					!ops[]:
						break;
				ops#push[c];
	else:
		res#push[c];
	i++;

ops[]::
	res#push[ops#pop[]];
	i--;

println["Postfix: " + res];

vals = new [];

i = 0;
i < res[]::
	c = res[i];
	operators ? c:
		v2 = vals#pop[];
		v1 = vals#pop[];
		r = calc[v1, v2, c];
		vals#push[r];
	else:
		vals#push[c as NUMBER];
	i++;
println["Result: " + vals[0]];
exit vals;