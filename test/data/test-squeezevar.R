# GSE112792; we're working with log2 transformed values so we skip the weighted regression steps

library(gemma.R)
library(SummarizedExperiment)
library(limma)
library(dplyr) 
dat <- getDataset("GSE112792")

e = assay(dat)
gt = factor(colData(dat)$genotype)

# some basic data filtering, not exactly the same as in Gemma but close
e<-e[apply(e, 1, var) > 1e-5,]
e<-e[apply(e, 1, function(x) {
  a<-length(unique(x))>5;
}),]


# test code for interpolation
x = c(9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0)
y = c(1.0, 0.25, 0.1111111111111111, 0.0625, 0.04, 0.027777777777777776, 0.02040816326530612, 0.015625, 0.012345679012345678, 0.01)
interpolate = c(9.5, 8.5, 7.5, 6.5, 5.5, 4.5, 3.5, 2.5, 1.5, 0.5)
f<-approxfun(x,y,rule=2)
f(c(interpolate))



f<-lmFit(e, model.matrix(~gt))
f<-eBayes(f)
write.table(f$sigma^2, "/Users/pzoot/test-vars.txt", quote=F, sep="\t")
limma::fitFDist(f$sigma^2, f$df.residual)

sv<-limma::squeezeVar(f$sigma^2, 6)
write.table(sv$var.post, "/Users/pzoot/test-var.post.txt", quote=F, sep='\t')

# raw count data from RNA seq pipeline, not via gemma
counts<-read.delim("./GSE112792_counts.genes", row.names=1, header=T)
# not quite equivalent to Gemma filters
counts<-counts[apply(counts,1,var)> 1e-5,]
counts<-counts[apply(counts, 1, function(x) {
  a<-length(unique(x))>5;
}),]

# fixme: sample and row order might not be right ...can work out from library sizes
vv<-voom(as.matrix(counts), model.matrix(~gt), plot=T)
gmv<-read.delim("mv-1885318782381326010.txt", header=F, sep='\t')
glo<-read.delim("loess-fit-943568072532947709.txt", header=F, sep='\t')
plot(gmv, pch='.', ylim=c(0,5))
lines(glo, col='red')

td<-read.delim("lmtest11.dat.txt", header=T, row.names=1, sep='\t')
tdes<-read.delim("lmtest11.des.txt", header=T, row.names=1, sep='\t')
des<-model.matrix(~tdes$targets.TreatmentDHT);
lib.size<-colSums(td)
tdlogcpm<-t(log2(t(td+0.5)/(lib.size+1)*1e6))
Amean<-rowMeans(tdlogcpm)
sx <- Amean+mean(log2(lib.size+1))-log2(1e6)
fit <- lmFit(tdlogcpm, des)
sy <- sqrt(fit$sigma)
l <- lowess(sx,sy,f=0.5)
write.table(l, "lmtest11.lowess.txt", quote=F, sep='\t')
write.table(tdlogcpm, "lmtest11.log2cpm.txt", quote=F, sep='\t')
tv<-voom(as.matrix(td), des, plot=T)
write.table(tv$weights, "lmtest11.voomweights.txt", sep='\t', quote=F)
fitted.values <- fit$coef %*% t(fit$design)
fitted.cpm <- 2^fitted.values
fitted.count <- 1e-6 * t(t(fitted.cpm)*(lib.size+1))
fitted.logcount <- log2(fitted.count)
f <- approxfun(l, rule=2)
w <- 1/f(fitted.logcount)^4
dim(w) <- dim(fitted.logcount)

