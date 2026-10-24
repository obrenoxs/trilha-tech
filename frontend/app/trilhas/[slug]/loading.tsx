import { LoadingRegion, SkeletonBar, StepCardSkeleton } from "@/components/ui/skeletons";

const infoCard =
  "flex min-w-0 flex-1 flex-col gap-3 rounded-16 border-3 border-ink bg-white px-5 pt-[18px] pb-5 shadow-sm md:px-[26px] md:pt-[22px] md:pb-6";

// Mesma estrutura da página da trilha: navegação, título, os dois cartões, métricas,
// botão do PDF, bloco de salário e a lista de etapas.
export default function Loading() {
  return (
    <div className="mx-auto w-full max-w-[1280px] px-5 md:px-24">
      <LoadingRegion>
        <div className="flex flex-col items-start gap-5 pt-3 pb-12 md:gap-6 md:pt-8 md:pb-[72px]">
          <SkeletonBar className="h-4 w-56" />
          <SkeletonBar className="h-9 w-3/4 max-w-[560px] md:h-[60px]" />
          <SkeletonBar className="h-5 w-full max-w-[720px]" />

          <div aria-hidden="true" className="flex w-full flex-col gap-4 md:flex-row md:gap-6">
            {[0, 1].map((item) => (
              <div key={item} className={infoCard}>
                <SkeletonBar className="h-4 w-36" />
                <SkeletonBar className="h-4 w-full" />
                <SkeletonBar className="h-4 w-5/6" />
              </div>
            ))}
          </div>

          <div aria-hidden="true" className="flex flex-wrap gap-2.5 md:gap-3.5">
            {[0, 1, 2].map((item) => (
              <SkeletonBar key={item} className="h-10 w-40 rounded-12 md:h-[46px]" />
            ))}
          </div>

          <SkeletonBar className="h-[58px] w-60 rounded-14" />

          <div
            aria-hidden="true"
            className="flex w-full flex-col gap-3 rounded-[18px] border-3 border-ink bg-white p-[18px] shadow-lg md:gap-[18px] md:rounded-[20px] md:px-8 md:py-6"
          >
            <SkeletonBar className="h-4 w-60" />
            <div className="flex flex-col gap-3 md:flex-row md:gap-6">
              {[0, 1, 2].map((item) => (
                <SkeletonBar key={item} className="h-[100px] rounded-14 md:flex-1" />
              ))}
            </div>
          </div>

          <SkeletonBar className="h-4 w-40" />
          <ul aria-hidden="true" className="flex w-full flex-col gap-5 md:gap-[22px]">
            {Array.from({ length: 4 }, (_, index) => (
              <li key={index}>
                <StepCardSkeleton index={index} />
              </li>
            ))}
          </ul>
        </div>
      </LoadingRegion>
    </div>
  );
}
